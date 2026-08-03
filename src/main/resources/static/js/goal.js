let currentGoal = null;

const goalForm = document.getElementById("goalForm");
const goalIdInput = document.getElementById("goalId");
const goalTitleInput = document.getElementById("goalTitle");
const goalDateInput = document.getElementById("goalDate");
const targetHoursInput = document.getElementById("targetHours");

const formTitle = document.getElementById("formTitle");
const saveGoalBtn = document.getElementById("saveGoalBtn");
const resetGoalBtn = document.getElementById("resetGoalBtn");

const goalMessage = document.getElementById("goalMessage");

const emptyGoal = document.getElementById("emptyGoal");
const currentGoalElement = document.getElementById("currentGoal");

const currentGoalTitle = document.getElementById("currentGoalTitle");
const currentGoalDate = document.getElementById("currentGoalDate");
const currentGoalDDay = document.getElementById("currentGoalDDay");
const currentGoalHours = document.getElementById("currentGoalHours");

const editGoalBtn = document.getElementById("editGoalBtn");
const deleteGoalBtn = document.getElementById("deleteGoalBtn");

function showMessage(message, isError = false) {
    goalMessage.textContent = message;
    goalMessage.style.color = isError
        ? "#d24e4e"
        : "#3c9a68";
}

function clearMessage() {
    goalMessage.textContent = "";
}

function resetForm() {
    goalForm.reset();
    goalIdInput.value = "";

    formTitle.textContent = "새 목표 등록";
    saveGoalBtn.textContent = "목표 저장";

    clearMessage();

    setMinimumGoalDate();
}

function setMinimumGoalDate() {
    const today = new Date();

    const year = today.getFullYear();
    const month = String(
        today.getMonth() + 1
    ).padStart(2, "0");

    const day = String(
        today.getDate()
    ).padStart(2, "0");

    goalDateInput.min =
        `${year}-${month}-${day}`;
}

function calculateDDay(goalDate) {
    const today = new Date();

    today.setHours(0, 0, 0, 0);

    const targetDate =
        new Date(`${goalDate}T00:00:00`);

    const differenceMs =
        targetDate.getTime() - today.getTime();

    return Math.ceil(
        differenceMs / (1000 * 60 * 60 * 24)
    );
}

function renderCurrentGoal(goal) {
    currentGoal = goal;

    if (!goal) {
        emptyGoal.hidden = false;
        currentGoalElement.hidden = true;
        return;
    }

    emptyGoal.hidden = true;
    currentGoalElement.hidden = false;

    currentGoalTitle.textContent =
        goal.goalTitle;

    currentGoalDate.textContent =
        goal.goalDate;

    const dDay =
        calculateDDay(goal.goalDate);

    if (dDay > 0) {
        currentGoalDDay.textContent =
            `D-${dDay}`;
    } else if (dDay === 0) {
        currentGoalDDay.textContent =
            "D-Day";
    } else {
        currentGoalDDay.textContent =
            `D+${Math.abs(dDay)}`;
    }

    const targetSeconds =
        Number(goal.targetSeconds ?? 0);

    const targetHours =
        targetSeconds / 3600;

    currentGoalHours.textContent =
        Number.isInteger(targetHours)
            ? `${targetHours}시간`
            : `${targetHours.toFixed(1)}시간`;
}

async function fetchJson(url, options = {}) {
    const response = await fetch(url, options);

    if (response.status === 204) {
        return null;
    }

    const responseText =
        await response.text();

    if (!response.ok) {
        throw new Error(
            responseText || "요청 처리에 실패했습니다."
        );
    }

    if (!responseText) {
        return null;
    }

    return JSON.parse(responseText);
}

async function loadCurrentGoal() {
    try {
        const goal =
            await fetchJson(
                "/api/goals/current"
            );

        renderCurrentGoal(goal);

    } catch (error) {
        console.error(error);

        showMessage(
            "목표 정보를 불러오지 못했습니다.",
            true
        );
    }
}

goalForm.addEventListener(
    "submit",
    async function (event) {
        event.preventDefault();

        clearMessage();

        const goalTitle =
            goalTitleInput.value.trim();

        const goalDate =
            goalDateInput.value;

        const targetHours =
            Number(targetHoursInput.value);

        if (!goalTitle) {
            showMessage(
                "목표명을 입력해주세요.",
                true
            );
            return;
        }

        if (!goalDate) {
            showMessage(
                "목표일을 선택해주세요.",
                true
            );
            return;
        }

        if (
            !Number.isFinite(targetHours)
            || targetHours < 1
            || targetHours > 24
        ) {
            showMessage(
                "하루 목표 공부시간은 1시간부터 24시간 사이로 입력해주세요.",
                true
            );
            return;
        }

        const requestBody = {
            goalTitle: goalTitle,
            goalDate: goalDate,
            targetSeconds:
                Math.round(targetHours * 3600)
        };

        const goalId =
            goalIdInput.value;

        const isEditing =
            Boolean(goalId);

        saveGoalBtn.disabled = true;
        saveGoalBtn.textContent =
            isEditing
                ? "수정 중..."
                : "저장 중...";

        try {
            const savedGoal =
                await fetchJson(
                    isEditing
                        ? `/api/goals/${goalId}`
                        : "/api/goals",
                    {
                        method:
                            isEditing
                                ? "PUT"
                                : "POST",

                        headers: {
                            "Content-Type":
                                "application/json"
                        },

                        body:
                            JSON.stringify(
                                requestBody
                            )
                    }
                );

            renderCurrentGoal(savedGoal);
            resetForm();

            showMessage(
                isEditing
                    ? "목표가 수정되었습니다."
                    : "목표가 등록되었습니다."
            );

        } catch (error) {
            console.error(error);

            showMessage(
                error.message,
                true
            );

        } finally {
            saveGoalBtn.disabled = false;
            saveGoalBtn.textContent =
                goalIdInput.value
                    ? "목표 수정"
                    : "목표 저장";
        }
    }
);

resetGoalBtn.addEventListener(
    "click",
    function () {
        resetForm();
    }
);

editGoalBtn.addEventListener(
    "click",
    function () {
        if (!currentGoal) {
            return;
        }

        goalIdInput.value =
            currentGoal.goalId;

        goalTitleInput.value =
            currentGoal.goalTitle ?? "";

        goalDateInput.value =
            currentGoal.goalDate ?? "";

        targetHoursInput.value =
            Number(
                currentGoal.targetSeconds ?? 0
            ) / 3600;

        formTitle.textContent =
            "목표 수정";

        saveGoalBtn.textContent =
            "목표 수정";

        clearMessage();

        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });
    }
);

deleteGoalBtn.addEventListener(
    "click",
    async function () {
        if (!currentGoal) {
            return;
        }

        const confirmed =
            window.confirm(
                "현재 목표를 삭제할까요?"
            );

        if (!confirmed) {
            return;
        }

        deleteGoalBtn.disabled = true;
        deleteGoalBtn.textContent =
            "삭제 중...";

        try {
            await fetchJson(
                `/api/goals/${currentGoal.goalId}`,
                {
                    method: "DELETE"
                }
            );

            currentGoal = null;

            renderCurrentGoal(null);
            resetForm();

            showMessage(
                "목표가 삭제되었습니다."
            );

        } catch (error) {
            console.error(error);

            showMessage(
                error.message,
                true
            );

        } finally {
            deleteGoalBtn.disabled = false;
            deleteGoalBtn.textContent =
                "삭제";
        }
    }
);

document.addEventListener(
    "DOMContentLoaded",
    async function () {
        setMinimumGoalDate();
        await loadCurrentGoal();
    }
);