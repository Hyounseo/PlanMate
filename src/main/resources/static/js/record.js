let tickInterval = null;
let timerStartMs = null;
let currentRecordId = null;

const timerDisplay = document.getElementById("timerDisplay");
const startBtn = document.getElementById("startBtn");
const stopBtn = document.getElementById("stopBtn");
const subjectSelect = document.getElementById("subjectSelect");
const statusMessage = document.getElementById("statusMessage");
const recordTableBody = document.getElementById("recordTableBody");

const subjectNameMap = new Map();

function formatHMS(totalSeconds) {
    const hours = String(
        Math.floor(totalSeconds / 3600)
    ).padStart(2, "0");

    const minutes = String(
        Math.floor((totalSeconds % 3600) / 60)
    ).padStart(2, "0");

    const seconds = String(
        Math.floor(totalSeconds % 60)
    ).padStart(2, "0");

    return `${hours}:${minutes}:${seconds}`;
}

function formatTime(timeValue) {
    if (!timeValue) {
        return "-";
    }

    return timeValue.length >= 8
        ? timeValue.substring(0, 8)
        : timeValue;
}

function showMessage(message, isError = true) {
    statusMessage.textContent = message;
    statusMessage.style.color = isError
        ? "#d24e4e"
        : "#3c9a68";
}

function clearMessage() {
    statusMessage.textContent = "";
}

function startTicking() {
    stopTicking();

    tickInterval = setInterval(() => {
        const elapsedSeconds = Math.max(
            0,
            Math.floor(
                (Date.now() - timerStartMs) / 1000
            )
        );

        timerDisplay.textContent =
            formatHMS(elapsedSeconds);
    }, 1000);
}

function stopTicking() {
    if (tickInterval !== null) {
        clearInterval(tickInterval);
        tickInterval = null;
    }
}

function setRunningUI(isRunning) {
    startBtn.disabled =
        isRunning || subjectSelect.options.length <= 1;

    stopBtn.disabled = !isRunning;
    subjectSelect.disabled = isRunning;
}

async function loadSubjects() {
    clearMessage();

    try {
        const response = await fetch("/api/subjects");

        if (!response.ok) {
            throw new Error(
                await response.text()
            );
        }

        const subjects = await response.json();

        subjectSelect.innerHTML = "";

        subjectNameMap.clear();

        if (subjects.length === 0) {
            const option =
                document.createElement("option");

            option.value = "";
            option.textContent =
                "등록된 과목이 없습니다";

            subjectSelect.appendChild(option);
            startBtn.disabled = true;

            showMessage(
                "일정 관리 화면에서 과목을 먼저 등록해주세요."
            );

            return;
        }

        const defaultOption =
            document.createElement("option");

        defaultOption.value = "";
        defaultOption.textContent =
            "과목을 선택하세요";

        subjectSelect.appendChild(defaultOption);

        subjects.forEach(subject => {
            subjectNameMap.set(
                String(subject.subjectId),
                subject.subjectName
            );

            const option =
                document.createElement("option");

            option.value = subject.subjectId;
            option.textContent =
                subject.subjectName;

            subjectSelect.appendChild(option);
        });

        startBtn.disabled = false;

    } catch (error) {
        console.error(error);

        subjectSelect.innerHTML =
            '<option value="">과목을 불러올 수 없습니다</option>';

        startBtn.disabled = true;

        showMessage(
            "과목 목록을 불러오지 못했습니다."
        );
    }
}

startBtn.addEventListener("click", async () => {
    clearMessage();

    const subjectId = subjectSelect.value;

    if (!subjectId) {
        showMessage("공부할 과목을 선택해주세요.");
        return;
    }

    try {
        const response = await fetch(
            `/api/study/start?subjectId=${encodeURIComponent(subjectId)}`,
            {
                method: "POST"
            }
        );

        if (!response.ok) {
            throw new Error(
                await response.text()
            );
        }

        const record = await response.json();

        currentRecordId = record.recordId;

        timerStartMs = new Date(
            `${record.studyDate}T${record.startTime}`
        ).getTime();

        timerDisplay.textContent = "00:00:00";

        setRunningUI(true);
        startTicking();

        showMessage(
            "타이머가 시작되었습니다.",
            false
        );

    } catch (error) {
        console.error(error);

        showMessage(
            "타이머 시작에 실패했습니다. 진행 중인 타이머가 있는지 확인해주세요."
        );
    }
});

stopBtn.addEventListener("click", async () => {
    if (!currentRecordId) {
        return;
    }

    clearMessage();

    try {
        const response = await fetch(
            `/api/study/stop/${currentRecordId}`,
            {
                method: "POST"
            }
        );

        if (!response.ok) {
            throw new Error(
                await response.text()
            );
        }

        stopTicking();

        currentRecordId = null;
        timerStartMs = null;

        timerDisplay.textContent = "00:00:00";

        setRunningUI(false);

        showMessage(
            "공부 기록이 저장되었습니다.",
            false
        );

        await loadTodayRecords();

    } catch (error) {
        console.error(error);

        showMessage(
            "타이머 종료에 실패했습니다. 새로고침 후 다시 시도해주세요."
        );
    }
});

async function restoreOngoingTimer() {
    try {
        const response = await fetch(
            "/api/study/ongoing"
        );

        if (response.status === 204) {
            return;
        }

        if (!response.ok) {
            return;
        }

        const responseText =
            await response.text();

        if (!responseText) {
            return;
        }

        const record =
            JSON.parse(responseText);

        if (!record) {
            return;
        }

        currentRecordId = record.recordId;

        subjectSelect.value =
            String(record.subjectId);

        timerStartMs = new Date(
            `${record.studyDate}T${record.startTime}`
        ).getTime();

        const elapsedSeconds = Math.max(
            0,
            Math.floor(
                (Date.now() - timerStartMs) / 1000
            )
        );

        timerDisplay.textContent =
            formatHMS(elapsedSeconds);

        setRunningUI(true);
        startTicking();

        showMessage(
            "진행 중인 타이머를 복구했습니다.",
            false
        );

    } catch (error) {
        console.error(error);
    }
}

async function loadTodayRecords() {
    try {
        const response = await fetch(
            "/api/study/list"
        );

        if (!response.ok) {
            throw new Error(
                await response.text()
            );
        }

        const records =
            await response.json();

        recordTableBody.innerHTML = "";

        if (records.length === 0) {
            const emptyRow =
                document.createElement("tr");

            emptyRow.className =
                "empty-record-row";

            emptyRow.innerHTML = `
                <td colspan="4">
                    오늘 등록된 공부 기록이 없습니다.
                </td>
            `;

            recordTableBody.appendChild(
                emptyRow
            );

            return;
        }

        records.forEach(record => {
            const row =
                document.createElement("tr");

            const subjectName =
                subjectNameMap.get(
                    String(record.subjectId)
                ) ?? `과목 ${record.subjectId}`;

            const durationText =
                record.durationSeconds != null
                    ? formatHMS(
                        record.durationSeconds
                    )
                    : "진행중";

            row.innerHTML = `
                <td>${escapeHtml(subjectName)}</td>
                <td>${formatTime(record.startTime)}</td>
                <td>${formatTime(record.endTime)}</td>
                <td>${durationText}</td>
            `;

            recordTableBody.appendChild(row);
        });

    } catch (error) {
        console.error(error);

        recordTableBody.innerHTML = `
            <tr class="empty-record-row">
                <td colspan="4">
                    공부 기록을 불러오지 못했습니다.
                </td>
            </tr>
        `;
    }
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

document.addEventListener(
    "DOMContentLoaded",
    async () => {
        await loadSubjects();
        await restoreOngoingTimer();
        await loadTodayRecords();
    }
);