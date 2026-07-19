document.addEventListener("DOMContentLoaded", () => {
    // ==============================
    // 1. HTML 요소 가져오기
    // ==============================

    // 과목 관리 영역
    const subjectList = document.getElementById("subjectList");
    const subjectNameInput = document.getElementById("subjectName");
    const subjectColorInput = document.getElementById("subjectColor");
    const subjectSelect = document.getElementById("subject");

    const addSubjectBtn = document.getElementById("addSubjectBtn");
    const updateSubjectBtn = document.getElementById("updateSubjectBtn");
    const deleteSubjectBtn = document.getElementById("deleteSubjectBtn");

    // 일정 입력 영역
    const titleInput = document.getElementById("title");
    const dateInput = document.getElementById("date");
    const startTimeInput = document.getElementById("startTime");
    const endTimeInput = document.getElementById("endTime");
    const memoInput = document.getElementById("memo");
    const statusSelect = document.getElementById("status");

    // 일정 폼 아래의 등록·수정·삭제 버튼
    const scheduleButtons = document.querySelectorAll(
        ".schedule-form-panel .button-row button"
    );

    const addScheduleBtn = scheduleButtons[0];
    const updateScheduleBtn = scheduleButtons[1];
    const deleteScheduleBtn = scheduleButtons[2];

    // 캘린더 영역
    const calendarTitle = document.querySelector(".calendar-header h3");
    const calendarButtons = document.querySelectorAll(
        ".calendar-header button"
    );

    const prevMonthBtn = calendarButtons[0];
    const nextMonthBtn = calendarButtons[1];
    const calendarDates = document.querySelector(".dates");

    // 위쪽 + 일정 등록 버튼
    const openScheduleFormBtn = document.querySelector(
        ".page-title .primary-button"
    );


    // ==============================
    // 2. 브라우저 저장소에서 데이터 불러오기
    // ==============================

    let subjects =
        JSON.parse(localStorage.getItem("planmateSubjects")) || [
            {
                name: "영어",
                color: "#6b7280"
            },
            {
                name: "자바",
                color: "#2563eb"
            },
            {
                name: "자료구조",
                color: "#16a34a"
            }
        ];

    let schedules =
        JSON.parse(localStorage.getItem("planmateSchedules")) || [];

    // 현재 선택한 과목 번호
    let selectedSubjectIndex = -1;

    // 현재 선택한 일정 ID
    let selectedScheduleId = null;

    // 현재 캘린더가 보여주는 달
    let currentCalendarDate = new Date();
    currentCalendarDate.setDate(1);


    // ==============================
    // 3. 데이터 저장 함수
    // ==============================

    function saveSubjects() {
        localStorage.setItem(
            "planmateSubjects",
            JSON.stringify(subjects)
        );
    }

    function saveSchedules() {
        localStorage.setItem(
            "planmateSchedules",
            JSON.stringify(schedules)
        );
    }


    // ==============================
    // 4. 과목 관리
    // ==============================

    function clearSubjectForm() {
        subjectNameInput.value = "";
        subjectColorInput.value = "#6b7280";
        selectedSubjectIndex = -1;
    }

    function renderSubjects() {
        const previousSelectedSubject = subjectSelect.value;

        subjectList.innerHTML = "";
        subjectSelect.innerHTML = "";

        if (subjects.length === 0) {
            const option = document.createElement("option");

            option.textContent = "과목을 먼저 추가하세요";
            option.value = "";

            subjectSelect.appendChild(option);
            return;
        }

        subjects.forEach((subject, index) => {
            // 왼쪽 과목 목록
            const listItem = document.createElement("li");

            if (index === selectedSubjectIndex) {
                listItem.classList.add("selected");
            }

            const colorCircle = document.createElement("span");
            colorCircle.className = "subject-color";
            colorCircle.style.backgroundColor = subject.color;

            const nameText = document.createElement("span");
            nameText.textContent = subject.name;

            listItem.appendChild(colorCircle);
            listItem.appendChild(nameText);

            // 과목을 클릭하면 수정할 과목으로 선택
            listItem.addEventListener("click", () => {
                selectedSubjectIndex = index;

                subjectNameInput.value = subject.name;
                subjectColorInput.value = subject.color;

                renderSubjects();
            });

            subjectList.appendChild(listItem);

            // 오른쪽 일정 폼의 과목 선택지
            const option = document.createElement("option");

            option.value = subject.name;
            option.textContent = subject.name;

            subjectSelect.appendChild(option);
        });

        // 기존에 선택했던 과목이 있으면 유지
        const subjectExists = subjects.some(
            (subject) => subject.name === previousSelectedSubject
        );

        if (subjectExists) {
            subjectSelect.value = previousSelectedSubject;
        }
    }

    addSubjectBtn.addEventListener("click", () => {
        const name = subjectNameInput.value.trim();
        const color = subjectColorInput.value;

        if (name === "") {
            alert("과목명을 입력해 주세요.");
            return;
        }

        const duplicate = subjects.some(
            (subject) => subject.name === name
        );

        if (duplicate) {
            alert("이미 등록된 과목입니다.");
            return;
        }

        subjects.push({
            name: name,
            color: color
        });

        saveSubjects();
        clearSubjectForm();
        renderSubjects();
    });

    updateSubjectBtn.addEventListener("click", () => {
        if (selectedSubjectIndex === -1) {
            alert("수정할 과목을 먼저 선택해 주세요.");
            return;
        }

        const newName = subjectNameInput.value.trim();
        const newColor = subjectColorInput.value;

        if (newName === "") {
            alert("과목명을 입력해 주세요.");
            return;
        }

        const duplicate = subjects.some((subject, index) => {
            return (
                subject.name === newName &&
                index !== selectedSubjectIndex
            );
        });

        if (duplicate) {
            alert("같은 이름의 과목이 이미 있습니다.");
            return;
        }

        const oldName = subjects[selectedSubjectIndex].name;

        subjects[selectedSubjectIndex] = {
            name: newName,
            color: newColor
        };

        // 과목 이름이 바뀌면 기존 일정의 과목 이름도 함께 변경
        schedules.forEach((schedule) => {
            if (schedule.subject === oldName) {
                schedule.subject = newName;
            }
        });

        saveSubjects();
        saveSchedules();

        clearSubjectForm();
        renderSubjects();
        renderCalendar();
    });

    deleteSubjectBtn.addEventListener("click", () => {
        if (selectedSubjectIndex === -1) {
            alert("삭제할 과목을 먼저 선택해 주세요.");
            return;
        }

        const selectedSubject = subjects[selectedSubjectIndex];

        const confirmed = confirm(
            `"${selectedSubject.name}" 과목과 관련 일정을 모두 삭제하시겠습니까?`
        );

        if (!confirmed) {
            return;
        }

        subjects.splice(selectedSubjectIndex, 1);

        // 삭제한 과목의 일정도 함께 제거
        schedules = schedules.filter((schedule) => {
            return schedule.subject !== selectedSubject.name;
        });

        saveSubjects();
        saveSchedules();

        clearSubjectForm();
        clearScheduleForm();

        renderSubjects();
        renderCalendar();
    });


    // ==============================
    // 5. 일정 입력 폼 관리
    // ==============================

    function clearScheduleForm() {
        titleInput.value = "";
        dateInput.value = "";
        startTimeInput.value = "";
        endTimeInput.value = "";
        memoInput.value = "";
        statusSelect.value = "예정";

        selectedScheduleId = null;
    }

    function validateScheduleForm() {
        if (subjects.length === 0) {
            alert("과목을 먼저 추가해 주세요.");
            return false;
        }

        if (titleInput.value.trim() === "") {
            alert("일정 제목을 입력해 주세요.");
            return false;
        }

        if (dateInput.value === "") {
            alert("날짜를 선택해 주세요.");
            return false;
        }

        if (startTimeInput.value === "") {
            alert("시작 시간을 선택해 주세요.");
            return false;
        }

        if (endTimeInput.value === "") {
            alert("종료 시간을 선택해 주세요.");
            return false;
        }

        if (endTimeInput.value <= startTimeInput.value) {
            alert("종료 시간은 시작 시간보다 늦어야 합니다.");
            return false;
        }

        return true;
    }

    function getScheduleFormData() {
        return {
            subject: subjectSelect.value,
            title: titleInput.value.trim(),
            date: dateInput.value,
            startTime: startTimeInput.value,
            endTime: endTimeInput.value,
            memo: memoInput.value.trim(),
            status: statusSelect.value
        };
    }

    // 일정 등록
    addScheduleBtn.addEventListener("click", () => {
        if (!validateScheduleForm()) {
            return;
        }

        const formData = getScheduleFormData();

        schedules.push({
            id: Date.now(),
            ...formData
        });

        saveSchedules();
        clearScheduleForm();
        renderCalendar();

        alert("일정이 등록되었습니다.");
    });

    // 일정 수정
    updateScheduleBtn.addEventListener("click", () => {
        if (selectedScheduleId === null) {
            alert("캘린더에서 수정할 일정을 먼저 선택해 주세요.");
            return;
        }

        if (!validateScheduleForm()) {
            return;
        }

        const scheduleIndex = schedules.findIndex(
            (schedule) => schedule.id === selectedScheduleId
        );

        if (scheduleIndex === -1) {
            alert("선택한 일정을 찾을 수 없습니다.");
            return;
        }

        schedules[scheduleIndex] = {
            id: selectedScheduleId,
            ...getScheduleFormData()
        };

        saveSchedules();
        clearScheduleForm();
        renderCalendar();

        alert("일정이 수정되었습니다.");
    });

    // 일정 삭제
    deleteScheduleBtn.addEventListener("click", () => {
        if (selectedScheduleId === null) {
            alert("캘린더에서 삭제할 일정을 먼저 선택해 주세요.");
            return;
        }

        const selectedSchedule = schedules.find(
            (schedule) => schedule.id === selectedScheduleId
        );

        if (!selectedSchedule) {
            return;
        }

        const confirmed = confirm(
            `"${selectedSchedule.title}" 일정을 삭제하시겠습니까?`
        );

        if (!confirmed) {
            return;
        }

        schedules = schedules.filter(
            (schedule) => schedule.id !== selectedScheduleId
        );

        saveSchedules();
        clearScheduleForm();
        renderCalendar();
    });


    // ==============================
    // 6. 캘린더 만들기
    // ==============================

    function makeDateKey(year, month, day) {
        const formattedMonth = String(month + 1).padStart(2, "0");
        const formattedDay = String(day).padStart(2, "0");

        return `${year}-${formattedMonth}-${formattedDay}`;
    }

    function getSubjectColor(subjectName) {
        const subject = subjects.find(
            (item) => item.name === subjectName
        );

        return subject ? subject.color : "#6b7280";
    }

    function selectSchedule(schedule) {
        selectedScheduleId = schedule.id;

        subjectSelect.value = schedule.subject;
        titleInput.value = schedule.title;
        dateInput.value = schedule.date;
        startTimeInput.value = schedule.startTime;
        endTimeInput.value = schedule.endTime;
        memoInput.value = schedule.memo;
        statusSelect.value = schedule.status;

        renderCalendar();
    }

    function renderCalendar() {
        const year = currentCalendarDate.getFullYear();
        const month = currentCalendarDate.getMonth();

        calendarTitle.textContent = `${year}년 ${month + 1}월`;
        calendarDates.innerHTML = "";

        // 해당 달 1일의 요일
        const firstDayIndex = new Date(year, month, 1).getDay();

        // 해당 달 마지막 날짜
        const lastDate = new Date(year, month + 1, 0).getDate();

        // 첫째 날 앞의 빈칸
        for (let i = 0; i < firstDayIndex; i++) {
            const emptyCell = document.createElement("div");
            emptyCell.className = "empty";

            calendarDates.appendChild(emptyCell);
        }

        // 날짜 생성
        for (let day = 1; day <= lastDate; day++) {
            const dateCell = document.createElement("div");
            const dateNumber = document.createElement("span");

            const dateKey = makeDateKey(year, month, day);

            dateNumber.textContent = day;
            dateCell.appendChild(dateNumber);

            // 날짜 칸을 클릭하면 일정 폼의 날짜 자동 입력
            dateCell.addEventListener("click", () => {
                dateInput.value = dateKey;
            });

            // 오늘 날짜 표시
            const today = new Date();

            if (
                year === today.getFullYear() &&
                month === today.getMonth() &&
                day === today.getDate()
            ) {
                dateCell.classList.add("today");
            }

            // 이 날짜에 등록된 일정만 가져오기
            const dateSchedules = schedules.filter(
                (schedule) => schedule.date === dateKey
            );

            dateSchedules.forEach((schedule) => {
                const scheduleItem = document.createElement("p");

                scheduleItem.className = "schedule-item";

                if (schedule.status === "완료") {
                    scheduleItem.classList.add("completed");
                }

                if (schedule.id === selectedScheduleId) {
                    scheduleItem.classList.add("selected-schedule");
                }

                scheduleItem.style.borderLeftColor =
                    getSubjectColor(schedule.subject);

                scheduleItem.textContent =
                    `${schedule.startTime} ${schedule.title}`;

                scheduleItem.title =
                    `${schedule.subject}\n` +
                    `${schedule.startTime}~${schedule.endTime}\n` +
                    `${schedule.memo}`;

                // 일정 클릭 시 수정·삭제할 일정으로 선택
                scheduleItem.addEventListener("click", (event) => {
                    event.stopPropagation();
                    selectSchedule(schedule);
                });

                dateCell.appendChild(scheduleItem);
            });

            calendarDates.appendChild(dateCell);
        }
    }

    // 이전 달
    prevMonthBtn.addEventListener("click", () => {
        currentCalendarDate.setMonth(
            currentCalendarDate.getMonth() - 1
        );

        renderCalendar();
    });

    // 다음 달
    nextMonthBtn.addEventListener("click", () => {
        currentCalendarDate.setMonth(
            currentCalendarDate.getMonth() + 1
        );

        renderCalendar();
    });

    // 위쪽 + 일정 등록 버튼
    openScheduleFormBtn.addEventListener("click", () => {
        clearScheduleForm();
        titleInput.focus();
    });


    // ==============================
    // 7. 처음 화면 표시
    // ==============================

    renderSubjects();
    renderCalendar();
});