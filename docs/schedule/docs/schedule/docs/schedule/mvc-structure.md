# 일정관리 MVC 구조 초안

## Model

### Schedule

- scheduleId
- userId
- subjectId
- title
- scheduleDate
- startTime
- endTime
- memo
- status

### Subject

- subjectId
- userId
- subjectName
- color
- targetTime

## View

### ScheduleView

- 캘린더 화면 표시
- 일정 목록 표시
- 일정 등록/수정 폼 표시

### SubjectView

- 과목 목록 표시
- 과목 추가/수정/삭제 화면 표시

## Controller

### ScheduleController

- 일정 등록
- 일정 수정
- 일정 삭제
- 날짜별 일정 조회
- 과목별 일정 조회

### SubjectController

- 과목 등록
- 과목 수정
- 과목 삭제
- 과목 목록 조회
