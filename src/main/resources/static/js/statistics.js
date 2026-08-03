let subjectChart = null;

function formatSeconds(totalSeconds) {
    if (totalSeconds == null) {
        return "0시간 0분";
    }

    const hours = Math.floor(totalSeconds / 3600);
    const minutes = Math.floor((totalSeconds % 3600) / 60);

    return `${hours}시간 ${minutes}분`;
}

function toLocalISODate(date) {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");

    return `${year}-${month}-${day}`;
}

function todayISO() {
    return toLocalISODate(new Date());
}

function firstDayOfMonthISO() {
    const now = new Date();

    return toLocalISODate(
        new Date(now.getFullYear(), now.getMonth(), 1)
    );
}

function lastDayOfMonthISO() {
    const now = new Date();

    return toLocalISODate(
        new Date(now.getFullYear(), now.getMonth() + 1, 0)
    );
}

async function fetchJson(url) {
    const response = await fetch(url);

    if (!response.ok) {
        const message = await response.text();
        throw new Error(message || "요청에 실패했습니다.");
    }

    return response.json();
}

async function loadSummaryCards() {
    const date = todayISO();

    const dailyElement = document.getElementById("dailyTotal");
    const weeklyElement = document.getElementById("weeklyTotal");
    const monthlyElement = document.getElementById("monthlyTotal");

    try {
        const [daily, weekly, monthly] = await Promise.all([
            fetchJson(
                `/api/statistics/daily?date=${encodeURIComponent(date)}`
            ),
            fetchJson(
                `/api/statistics/weekly?date=${encodeURIComponent(date)}`
            ),
            fetchJson(
                `/api/statistics/monthly?date=${encodeURIComponent(date)}`
            )
        ]);

        dailyElement.textContent =
            formatSeconds(daily.totalSeconds);

        weeklyElement.textContent =
            formatSeconds(weekly.totalSeconds);

        monthlyElement.textContent =
            formatSeconds(monthly.totalSeconds);

    } catch (error) {
        console.error(error);

        dailyElement.textContent = "불러오기 실패";
        weeklyElement.textContent = "불러오기 실패";
        monthlyElement.textContent = "불러오기 실패";
    }
}

async function loadSubjectChart() {
    const startDate = firstDayOfMonthISO();
    const endDate = lastDayOfMonthISO();

    const chartCanvas =
        document.getElementById("subjectChart");

    const emptyMessage =
        document.getElementById("chartEmptyMessage");

    try {
        const [subjectStatistics, subjects] =
            await Promise.all([
                fetchJson(
                    `/api/statistics/by-subject`
                    + `?startDate=${encodeURIComponent(startDate)}`
                    + `&endDate=${encodeURIComponent(endDate)}`
                ),
                fetchJson("/api/subjects")
            ]);

        const subjectNameMap = new Map();

        subjects.forEach(subject => {
            subjectNameMap.set(
                String(subject.subjectId),
                subject.subjectName
            );
        });

        const subjectEntries =
            Object.entries(subjectStatistics)
                .filter(([, totalSeconds]) =>
                    Number(totalSeconds) > 0
                )
                .sort((a, b) =>
                    Number(b[1]) - Number(a[1])
                );

        if (subjectEntries.length === 0) {
            chartCanvas.style.display = "none";
            emptyMessage.style.display = "block";

            if (subjectChart) {
                subjectChart.destroy();
                subjectChart = null;
            }

            return;
        }

        chartCanvas.style.display = "block";
        emptyMessage.style.display = "none";

        const labels = subjectEntries.map(([subjectId]) =>
            subjectNameMap.get(String(subjectId))
            ?? `과목 ${subjectId}`
        );

        const hours = subjectEntries.map(([, totalSeconds]) =>
            Math.round(
                (Number(totalSeconds) / 3600) * 10
            ) / 10
        );

        if (subjectChart) {
            subjectChart.destroy();
        }

        subjectChart = new Chart(chartCanvas, {
            type: "bar",

            data: {
                labels: labels,

                datasets: [
                    {
                        label: "공부시간(시간)",
                        data: hours,
                        backgroundColor:
                            "rgba(108, 99, 255, 0.72)",
                        borderColor:
                            "rgba(108, 99, 255, 1)",
                        borderWidth: 1,
                        borderRadius: 8,
                        maxBarThickness: 58
                    }
                ]
            },

            options: {
                responsive: true,
                maintainAspectRatio: false,

                plugins: {
                    legend: {
                        display: false
                    },

                    tooltip: {
                        callbacks: {
                            label(context) {
                                return `${context.raw}시간`;
                            }
                        }
                    }
                },

                scales: {
                    x: {
                        grid: {
                            display: false
                        },

                        ticks: {
                            color: "#667487"
                        }
                    },

                    y: {
                        beginAtZero: true,

                        grid: {
                            color: "#edf0f4"
                        },

                        ticks: {
                            color: "#667487"
                        },

                        title: {
                            display: true,
                            text: "시간",
                            color: "#667487"
                        }
                    }
                }
            }
        });

    } catch (error) {
        console.error(error);

        chartCanvas.style.display = "none";
        emptyMessage.style.display = "block";
        emptyMessage.textContent =
            "통계 데이터를 불러오지 못했습니다.";
    }
}

document.addEventListener(
    "DOMContentLoaded",
    async function () {
        await Promise.all([
            loadSummaryCards(),
            loadSubjectChart()
        ]);
    }
);