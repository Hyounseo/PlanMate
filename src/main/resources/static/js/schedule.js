document.addEventListener("DOMContentLoaded", () => {

    // ==============================
    // 1. HTML 요소 가져오기
    // ==============================

    // 과목 관리
    const subjectList =
        document.getElementById("subjectList");

    const subjectNameInput =
        document.getElementById("subjectName");

    const subjectColorInput =
        document.getElementById("subjectColor");

    const subjectSelect =
        document.getElementById("subject");

    const addSubjectBtn =
        document.getElementById("addSubjectBtn");

    const updateSubjectBtn =
        document.getElementById("updateSubjectBtn");

    const deleteSubjectBtn =
        document.getElementById("deleteSubjectBtn");

    // 일정 입력
    const titleInput =
        document.getElementById("title");

    const dateInput =
        document.getElementById("date");

    const startTimeInput =
        document.getElementById("startTime");

    const endTimeInput =
        document.getElementById("endTime");

    const memoInput =
        document.getElementById("memo");

    const statusSelect =
        document.getElementById("status");

    // 일정 등록·수정·삭제 버튼
    const addScheduleBtn =
        document.getElementById("addScheduleBtn");

    const updateScheduleBtn =
        document.getElementById("updateScheduleBtn");

    const deleteScheduleBtn =
        document.getElementById("deleteScheduleBtn");

    // 캘린더
    const calendarTitle =
        document.getElementById("calendarTitle");

    const prevMonthBtn =
        document.getElementById("prevMonthBtn");

    const nextMonthBtn =
        document.getElementById("nextMonthBtn");

    const calendarDates =
        document.getElementById("calendarDates");

    const openScheduleFormBtn =
        document.getElementById("openScheduleFormBtn");


    // ==============================
    // 2. 화면에서 사용할 데이터
    // ==============================

    let subjects = [];
    let schedules = [];

    // 수정·삭제 대상으로 선택한 과목 ID
    let selectedSubjectId = null;

    // 수정·삭제 대상으로 선택한 일정 ID
    let selectedScheduleId = null;

    // 사용자가 캘린더에서 선택한 날짜
    let selectedDate = null;

    // 현재 캘린더가 보여주는 달
    const currentCalendarDate = new Date();
    currentCalendarDate.setDate(1);


    // ==============================
    // 3. 서버 요청 공통 함수
    // ==============================

    async function request(url, options = {}) {

        const response = await fetch(url, {
            headers: {
                "Content-Type": "application/json",
                ...options.headers
            },
            ...options
        });

        if (!response.ok) {

            let message =
                "요청 처리 중 오류가 발생했습니다.";

            try {

                const errorData =
                    await response.json();

                if (errorData.message) {
                    message = errorData.message;
                }

            } catch (error) {
                // JSON 오류 응답이 아니면 기본 문구 사용
            }

            throw new Error(message);
        }

        // 삭제 성공처럼 응답 내용이 없는 경우
        if (response.status === 204) {
            return null;
        }

        return response.json();
    }


    // ==============================
    // 4. 과목 API
    // ==============================

    async function loadSubjects() {

        try {

            subjects =
                await request("/api/subjects");

            renderSubjects();

        } catch (error) {

            console.error(error);

            alert(
                "과목 목록을 불러오지 못했습니다."
            );
        }
    }

    async function createSubject(subjectData) {

        return request("/api/subjects", {
            method: "POST",
            body: JSON.stringify(subjectData)
        });
    }

    async function updateSubject(
        subjectId,
        subjectData
    ) {

        return request(
            `/api/subjects/${subjectId}`,
            {
                method: "PUT",
                body: JSON.stringify(subjectData)
            }
        );
    }

    async function deleteSubject(subjectId) {

        return request(
            `/api/subjects/${subjectId}`,
            {
                method: "DELETE"
            }
        );
    }


    // ==============================
    // 5. 일정 API
    // ==============================

    function getCurrentMonthRange() {

        const year =
            currentCalendarDate.getFullYear();

        const month =
            currentCalendarDate.getMonth();

        const lastDay =
            new Date(
                year,
                month + 1,
                0
            ).getDate();

        return {
            startDate:
                makeDateKey(
                    year,
                    month,
                    1
                ),

            endDate:
                makeDateKey(
                    year,
                    month,
                    lastDay
                )
        };
    }

    async function loadSchedules() {

        const {
            startDate,
            endDate
        } = getCurrentMonthRange();

        try {

            schedules = await request(
                `/api/schedules?startDate=${startDate}&endDate=${endDate}`
            );

            renderCalendar();

        } catch (error) {

            console.error(error);

            alert(
                "일정 목록을 불러오지 못했습니다."
            );
        }
    }

    async function createSchedule(
        scheduleData
    ) {

        return request("/api/schedules", {
            method: "POST",
            body: JSON.stringify(scheduleData)
        });
    }

    async function updateSchedule(
        scheduleId,
        scheduleData
    ) {

        return request(
            `/api/schedules/${scheduleId}`,
            {
                method: "PUT",
                body: JSON.stringify(scheduleData)
            }
        );
    }

    async function deleteSchedule(
        scheduleId
    ) {

        return request(
            `/api/schedules/${scheduleId}`,
            {
                method: "DELETE"
            }
        );
    }


    // ==============================
    // 6. 과목 화면 관리
    // ==============================

    function clearSubjectForm() {

        subjectNameInput.value = "";
        subjectColorInput.value = "#6b7280";

        selectedSubjectId = null;
    }

    function renderSubjects() {

        const previousSelectedSubjectId =
            subjectSelect.value;

        subjectList.innerHTML = "";
        subjectSelect.innerHTML = "";

        if (subjects.length === 0) {

            const option =
                document.createElement("option");

            option.textContent =
                "과목을 먼저 추가하세요";

            option.value = "";

            subjectSelect.appendChild(option);

            return;
        }

        subjects.forEach((subject) => {

            // 왼쪽 과목 목록
            const listItem =
                document.createElement("li");

            if (
                subject.subjectId
                === selectedSubjectId
            ) {
                listItem.classList.add(
                    "selected"
                );
            }

            const colorCircle =
                document.createElement("span");

            colorCircle.className =
                "subject-color";

            colorCircle.style.backgroundColor =
                subject.color;

            const nameText =
                document.createElement("span");

            nameText.textContent =
                subject.subjectName;

            listItem.appendChild(
                colorCircle
            );

            listItem.appendChild(
                nameText
            );

            listItem.addEventListener(
                "click",
                () => {

                    selectedSubjectId =
                        subject.subjectId;

                    subjectNameInput.value =
                        subject.subjectName;

                    subjectColorInput.value =
                        subject.color;

                    renderSubjects();
                }
            );

            subjectList.appendChild(
                listItem
            );

            // 일정 폼 과목 선택지
            const option =
                document.createElement("option");

            option.value =
                String(subject.subjectId);

            option.textContent =
                subject.subjectName;

            subjectSelect.appendChild(
                option
            );
        });

        // 기존 선택 과목 유지
        const subjectExists =
            subjects.some((subject) => {

                return (
                    String(subject.subjectId)
                    === previousSelectedSubjectId
                );
            });

        if (subjectExists) {

            subjectSelect.value =
                previousSelectedSubjectId;
        }
    }


    // 과목 등록
    addSubjectBtn.addEventListener(
        "click",
        async () => {

            const subjectName =
                subjectNameInput.value.trim();

            const color =
                subjectColorInput.value;

            if (subjectName === "") {

                alert(
                    "과목명을 입력해 주세요."
                );

                return;
            }

            const duplicate =
                subjects.some((subject) => {

                    return (
                        subject.subjectName
                        === subjectName
                    );
                });

            if (duplicate) {

                alert(
                    "이미 등록된 과목입니다."
                );

                return;
            }

            try {

                await createSubject({
                    subjectName: subjectName,
                    color: color,
                    targetTime: null
                });

                clearSubjectForm();

                await loadSubjects();

                alert(
                    "과목이 등록되었습니다."
                );

            } catch (error) {

                console.error(error);
                alert(error.message);
            }
        }
    );


    // 과목 수정
    updateSubjectBtn.addEventListener(
        "click",
        async () => {

            if (selectedSubjectId === null) {

                alert(
                    "수정할 과목을 먼저 선택해 주세요."
                );

                return;
            }

            const subjectName =
                subjectNameInput.value.trim();

            const color =
                subjectColorInput.value;

            if (subjectName === "") {

                alert(
                    "과목명을 입력해 주세요."
                );

                return;
            }

            const duplicate =
                subjects.some((subject) => {

                    return (
                        subject.subjectName
                        === subjectName
                        &&
                        subject.subjectId
                        !== selectedSubjectId
                    );
                });

            if (duplicate) {

                alert(
                    "같은 이름의 과목이 이미 있습니다."
                );

                return;
            }

            try {

                await updateSubject(
                    selectedSubjectId,
                    {
                        subjectName: subjectName,
                        color: color,
                        targetTime: null
                    }
                );

                clearSubjectForm();

                await loadSubjects();
                await loadSchedules();

                alert(
                    "과목이 수정되었습니다."
                );

            } catch (error) {

                console.error(error);
                alert(error.message);
            }
        }
    );


    // 과목 삭제
    deleteSubjectBtn.addEventListener(
        "click",
        async () => {

            if (selectedSubjectId === null) {

                alert(
                    "삭제할 과목을 먼저 선택해 주세요."
                );

                return;
            }

            const selectedSubject =
                subjects.find((subject) => {

                    return (
                        subject.subjectId
                        === selectedSubjectId
                    );
                });

            if (!selectedSubject) {

                alert(
                    "선택한 과목을 찾을 수 없습니다."
                );

                return;
            }

            const confirmed = confirm(
                `"${selectedSubject.subjectName}" 과목과 관련 일정을 모두 삭제하시겠습니까?`
            );

            if (!confirmed) {
                return;
            }

            try {

                await deleteSubject(
                    selectedSubjectId
                );

                clearSubjectForm();
                clearScheduleForm();

                await loadSubjects();
                await loadSchedules();

                alert(
                    "과목이 삭제되었습니다."
                );

            } catch (error) {

                console.error(error);
                alert(error.message);
            }
        }
    );


    // ==============================
    // 7. 일정 입력 폼
    // ==============================

    function clearScheduleForm() {

        titleInput.value = "";
        dateInput.value = "";
        startTimeInput.value = "";
        endTimeInput.value = "";
        memoInput.value = "";

        statusSelect.value = "예정";

        selectedScheduleId = null;
        selectedDate = null;

        renderCalendar();
    }

    function validateScheduleForm() {

        if (
            subjects.length === 0
            || subjectSelect.value === ""
        ) {

            alert(
                "과목을 먼저 추가해 주세요."
            );

            return false;
        }

        if (
            titleInput.value.trim()
            === ""
        ) {

            alert(
                "일정 제목을 입력해 주세요."
            );

            return false;
        }

        if (dateInput.value === "") {

            alert(
                "날짜를 선택해 주세요."
            );

            return false;
        }

        if (startTimeInput.value === "") {

            alert(
                "시작 시간을 선택해 주세요."
            );

            return false;
        }

        if (endTimeInput.value === "") {

            alert(
                "종료 시간을 선택해 주세요."
            );

            return false;
        }

        if (
            endTimeInput.value
            <= startTimeInput.value
        ) {

            alert(
                "종료 시간은 시작 시간보다 늦어야 합니다."
            );

            return false;
        }

        return true;
    }

    function getScheduleFormData() {

        return {
            subjectId:
                Number(subjectSelect.value),

            title:
                titleInput.value.trim(),

            scheduleDate:
            dateInput.value,

            startTime:
            startTimeInput.value,

            endTime:
            endTimeInput.value,

            memo:
                memoInput.value.trim(),

            status:
            statusSelect.value
        };
    }


    // 일정 등록
    addScheduleBtn.addEventListener(
        "click",
        async () => {

            if (!validateScheduleForm()) {
                return;
            }

            try {

                await createSchedule(
                    getScheduleFormData()
                );

                clearScheduleForm();

                await loadSchedules();

                alert(
                    "일정이 등록되었습니다."
                );

            } catch (error) {

                console.error(error);
                alert(error.message);
            }
        }
    );


    // 일정 수정
    updateScheduleBtn.addEventListener(
        "click",
        async () => {

            if (
                selectedScheduleId
                === null
            ) {

                alert(
                    "캘린더에서 수정할 일정을 먼저 선택해 주세요."
                );

                return;
            }

            if (!validateScheduleForm()) {
                return;
            }

            try {

                await updateSchedule(
                    selectedScheduleId,
                    getScheduleFormData()
                );

                clearScheduleForm();

                await loadSchedules();

                alert(
                    "일정이 수정되었습니다."
                );

            } catch (error) {

                console.error(error);
                alert(error.message);
            }
        }
    );


    // 일정 삭제
    deleteScheduleBtn.addEventListener(
        "click",
        async () => {

            if (
                selectedScheduleId
                === null
            ) {

                alert(
                    "캘린더에서 삭제할 일정을 먼저 선택해 주세요."
                );

                return;
            }

            const selectedSchedule =
                schedules.find((schedule) => {

                    return (
                        schedule.scheduleId
                        === selectedScheduleId
                    );
                });

            if (!selectedSchedule) {

                alert(
                    "선택한 일정을 찾을 수 없습니다."
                );

                return;
            }

            const confirmed = confirm(
                `"${selectedSchedule.title}" 일정을 삭제하시겠습니까?`
            );

            if (!confirmed) {
                return;
            }

            try {

                await deleteSchedule(
                    selectedScheduleId
                );

                clearScheduleForm();

                await loadSchedules();

                alert(
                    "일정이 삭제되었습니다."
                );

            } catch (error) {

                console.error(error);
                alert(error.message);
            }
        }
    );


    // ==============================
    // 8. 캘린더
    // ==============================

    function makeDateKey(
        year,
        month,
        day
    ) {

        const formattedMonth =
            String(month + 1)
                .padStart(2, "0");

        const formattedDay =
            String(day)
                .padStart(2, "0");

        return (
            `${year}-${formattedMonth}-${formattedDay}`
        );
    }

    function getSubject(subjectId) {

        return subjects.find(
            (subject) => {

                return (
                    subject.subjectId
                    === subjectId
                );
            }
        );
    }

    function getSubjectColor(subjectId) {

        const subject =
            getSubject(subjectId);

        return subject
            ? subject.color
            : "#6b7280";
    }

    function getSubjectName(subjectId) {

        const subject =
            getSubject(subjectId);

        return subject
            ? subject.subjectName
            : "알 수 없는 과목";
    }

    function selectSchedule(schedule) {

        selectedScheduleId =
            schedule.scheduleId;

        selectedDate =
            schedule.scheduleDate;

        subjectSelect.value =
            String(schedule.subjectId);

        titleInput.value =
            schedule.title;

        dateInput.value =
            schedule.scheduleDate;

        startTimeInput.value =
            schedule.startTime || "";

        endTimeInput.value =
            schedule.endTime || "";

        memoInput.value =
            schedule.memo || "";

        statusSelect.value =
            schedule.status;

        renderCalendar();
    }

    function renderCalendar() {

        const year =
            currentCalendarDate.getFullYear();

        const month =
            currentCalendarDate.getMonth();

        calendarTitle.textContent =
            `${year}년 ${month + 1}월`;

        calendarDates.innerHTML = "";

        const firstDayIndex =
            new Date(
                year,
                month,
                1
            ).getDay();

        const lastDate =
            new Date(
                year,
                month + 1,
                0
            ).getDate();

        const today = new Date();

        // 첫째 날 앞의 빈칸
        for (
            let i = 0;
            i < firstDayIndex;
            i++
        ) {

            const emptyCell =
                document.createElement("div");

            emptyCell.className =
                "empty";

            calendarDates.appendChild(
                emptyCell
            );
        }

        // 날짜 생성
        for (
            let day = 1;
            day <= lastDate;
            day++
        ) {

            const dateCell =
                document.createElement("div");

            const dateNumber =
                document.createElement("span");

            const dateKey =
                makeDateKey(
                    year,
                    month,
                    day
                );

            dateCell.className =
                "date-cell";

            dateCell.dataset.date =
                dateKey;

            dateNumber.className =
                "date-number";

            dateNumber.textContent =
                day;

            dateCell.appendChild(
                dateNumber
            );

            // 날짜 클릭
            dateCell.addEventListener(
                "click",
                () => {

                    selectedDate =
                        dateKey;

                    dateInput.value =
                        dateKey;

                    renderCalendar();
                }
            );

            // 오늘 날짜 표시
            if (
                year
                === today.getFullYear()
                &&
                month
                === today.getMonth()
                &&
                day
                === today.getDate()
            ) {

                dateCell.classList.add(
                    "today"
                );
            }

            // 선택한 날짜 표시
            if (
                selectedDate
                === dateKey
            ) {

                dateCell.classList.add(
                    "selected-date"
                );
            }

            // 해당 날짜 일정 조회
            const dateSchedules =
                schedules.filter(
                    (schedule) => {

                        return (
                            schedule.scheduleDate
                            === dateKey
                        );
                    }
                );

            dateSchedules.forEach(
                (schedule) => {

                    const scheduleItem =
                        document.createElement(
                            "div"
                        );

                    scheduleItem.className =
                        "schedule-item";

                    scheduleItem.dataset.scheduleId =
                        String(
                            schedule.scheduleId
                        );

                    // 완료 일정
                    if (
                        schedule.status
                        === "완료"
                        ||
                        schedule.status
                        === "COMPLETED"
                    ) {

                        scheduleItem.classList.add(
                            "completed"
                        );
                    }

                    // 선택된 일정
                    if (
                        schedule.scheduleId
                        === selectedScheduleId
                    ) {

                        scheduleItem.classList.add(
                            "selected-schedule"
                        );
                    }

                    scheduleItem.style.borderLeftColor =
                        getSubjectColor(
                            schedule.subjectId
                        );

                    // 일정 시간
                    const scheduleTime =
                        document.createElement(
                            "div"
                        );

                    scheduleTime.className =
                        "schedule-time";

                    scheduleTime.textContent =
                        schedule.startTime
                        || "시간 미지정";

                    // 일정 제목
                    const scheduleTitle =
                        document.createElement(
                            "div"
                        );

                    scheduleTitle.className =
                        "schedule-title";

                    scheduleTitle.textContent =
                        schedule.title;

                    // 툴팁
                    scheduleItem.title =
                        `${getSubjectName(schedule.subjectId)}\n`
                        + `${schedule.startTime || "시간 미지정"}`
                        + ` ~ ${schedule.endTime || "시간 미지정"}\n`
                        + `${schedule.title}\n`
                        + `${schedule.memo || "메모 없음"}`;

                    scheduleItem.appendChild(
                        scheduleTime
                    );

                    scheduleItem.appendChild(
                        scheduleTitle
                    );

                    // 일정 클릭
                    scheduleItem.addEventListener(
                        "click",
                        (event) => {

                            event.stopPropagation();

                            selectSchedule(
                                schedule
                            );
                        }
                    );

                    dateCell.appendChild(
                        scheduleItem
                    );
                }
            );

            calendarDates.appendChild(
                dateCell
            );
        }
    }


    // 이전 달
    prevMonthBtn.addEventListener(
        "click",
        async () => {

            currentCalendarDate.setMonth(
                currentCalendarDate.getMonth()
                - 1
            );

            selectedDate = null;
            selectedScheduleId = null;

            await loadSchedules();
        }
    );


    // 다음 달
    nextMonthBtn.addEventListener(
        "click",
        async () => {

            currentCalendarDate.setMonth(
                currentCalendarDate.getMonth()
                + 1
            );

            selectedDate = null;
            selectedScheduleId = null;

            await loadSchedules();
        }
    );


    // 위쪽 일정 등록 버튼
    if (openScheduleFormBtn) {

        openScheduleFormBtn.addEventListener(
            "click",
            () => {

                clearScheduleForm();
                titleInput.focus();
            }
        );
    }


    // ==============================
    // 9. 처음 화면 표시
    // ==============================

    async function initialize() {

        await loadSubjects();
        await loadSchedules();
    }

    initialize();
});