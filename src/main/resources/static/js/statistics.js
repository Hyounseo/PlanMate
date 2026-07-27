// 초(seconds)를 "N시간 M분" 형태로 변환
function formatSeconds(totalSeconds) {
    if (totalSeconds == null) return '--';
    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);
    return `${hours}시간 ${minutes}분`;
}

function todayISO() {
    return new Date().toISOString().slice(0, 10); // YYYY-MM-DD
}

function firstDayOfMonthISO() {
    const now = new Date();
    return new Date(now.getFullYear(), now.getMonth(), 1).toISOString().slice(0, 10);
}

function lastDayOfMonthISO() {
    const now = new Date();
    return new Date(now.getFullYear(), now.getMonth() + 1, 0).toISOString().slice(0, 10);
}

// 요약 카드(일/주/월) 채우기
async function loadSummaryCards() {
    const date = todayISO();

    const [daily, weekly, monthly] = await Promise.all([
        fetch(`/api/statistics/daily?date=${date}`).then(r => r.json()),
        fetch(`/api/statistics/weekly?date=${date}`).then(r => r.json()),
        fetch(`/api/statistics/monthly?date=${date}`).then(r => r.json())
    ]);

    document.getElementById('dailyTotal').textContent = formatSeconds(daily.totalSeconds);
    document.getElementById('weeklyTotal').textContent = formatSeconds(weekly.totalSeconds);
    document.getElementById('monthlyTotal').textContent = formatSeconds(monthly.totalSeconds);
}

// 과목별 통계 -> Chart.js 막대그래프
async function loadSubjectChart() {
    const startDate = firstDayOfMonthISO();
    const endDate = lastDayOfMonthISO();

    const data = await fetch(`/api/statistics/by-subject?startDate=${startDate}&endDate=${endDate}`)
        .then(r => r.json());
    // data 형태: { "1": 3600, "2": 7200 } (subjectId -> 초)

    // TODO: 팀원2(Subject) API 연동되면 subjectId -> 과목명으로 교체
    const labels = Object.keys(data).map(id => `과목 ${id}`);
    const seconds = Object.values(data);
    const hours = seconds.map(s => Math.round((s / 3600) * 10) / 10); // 그래프는 시간 단위로 보여줌

    new Chart(document.getElementById('subjectChart'), {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: '공부시간(시간)',
                data: hours,
                backgroundColor: '#7C9EFF'
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true,
                    title: { display: true, text: '시간' }
                }
            }
        }
    });
}

document.addEventListener('DOMContentLoaded', () => {
    loadSummaryCards();
    loadSubjectChart();
});
