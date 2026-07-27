let tickInterval = null;
let timerStartMs = null;
let currentRecordId = null;

const timerDisplay = document.getElementById('timerDisplay');
const startBtn = document.getElementById('startBtn');
const stopBtn = document.getElementById('stopBtn');
const subjectSelect = document.getElementById('subjectSelect');
const statusMessage = document.getElementById('statusMessage');

function formatHMS(totalSeconds) {
    const h = String(Math.floor(totalSeconds / 3600)).padStart(2, '0');
    const m = String(Math.floor((totalSeconds % 3600) / 60)).padStart(2, '0');
    const s = String(Math.floor(totalSeconds % 60)).padStart(2, '0');
    return `${h}:${m}:${s}`;
}

function startTicking() {
    tickInterval = setInterval(() => {
        const elapsedSec = Math.floor((Date.now() - timerStartMs) / 1000);
        timerDisplay.textContent = formatHMS(elapsedSec);
    }, 1000);
}

function stopTicking() {
    clearInterval(tickInterval);
    tickInterval = null;
}

function setRunningUI(isRunning) {
    startBtn.disabled = isRunning;
    stopBtn.disabled = !isRunning;
    subjectSelect.disabled = isRunning;
}

// 시작 버튼
startBtn.addEventListener('click', async () => {
    statusMessage.textContent = '';
    const subjectId = subjectSelect.value;

    try {
        const res = await fetch(`/api/study/start?subjectId=${subjectId}`, { method: 'POST' });
        if (!res.ok) throw new Error(await res.text());
        const record = await res.json();

        currentRecordId = record.recordId;
        timerStartMs = new Date(`${record.studyDate}T${record.startTime}`).getTime();
        timerDisplay.textContent = '00:00:00';
        setRunningUI(true);
        startTicking();
    } catch (err) {
        statusMessage.textContent = '타이머 시작 실패: 이미 진행중인 타이머가 있는지 확인해주세요.';
    }
});

// 종료 버튼
stopBtn.addEventListener('click', async () => {
    if (!currentRecordId) return;

    try {
        const res = await fetch(`/api/study/stop/${currentRecordId}`, { method: 'POST' });
        if (!res.ok) throw new Error(await res.text());

        stopTicking();
        setRunningUI(false);
        timerDisplay.textContent = '00:00:00';
        currentRecordId = null;
        loadTodayRecords(); // 리스트 갱신
    } catch (err) {
        statusMessage.textContent = '타이머 종료 실패. 새로고침 후 다시 시도해주세요.';
    }
});

// 페이지 로드 시 진행중인 타이머가 있으면 복구 (새로고침 대응)
async function restoreOngoingTimer() {
    const res = await fetch('/api/study/ongoing');
    if (!res.ok) return;
    const record = await res.json();
    if (!record) return;

    currentRecordId = record.recordId;
    subjectSelect.value = record.subjectId;
    timerStartMs = new Date(`${record.studyDate}T${record.startTime}`).getTime();
    setRunningUI(true);
    startTicking();
}

// 오늘 기록 리스트 불러오기
async function loadTodayRecords() {
    const res = await fetch('/api/study/list');
    if (!res.ok) return;
    const records = await res.json();

    const tbody = document.getElementById('recordTableBody');
    tbody.innerHTML = '';

    records.forEach(r => {
        const row = document.createElement('tr');
        const durationText = r.durationSeconds != null ? formatHMS(r.durationSeconds) : '진행중';
        row.innerHTML = `
            <td>과목 ${r.subjectId}</td>
            <td>${r.startTime}</td>
            <td>${r.endTime ?? '-'}</td>
            <td>${durationText}</td>
        `;
        tbody.appendChild(row);
    });
}

document.addEventListener('DOMContentLoaded', () => {
    restoreOngoingTimer();
    loadTodayRecords();
});