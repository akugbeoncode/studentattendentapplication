

class TimeTable {
    constructor(activityStartTime, classDuration, longBreak, shortBreak, numberOfClassesPerDay) {
        this.activityStartTime = activityStartTime
        this.classDuration = classDuration
        this.longBreak = longBreak
        this.shortBreak = shortBreak
        this.numberOfClassesPerDay = numberOfClassesPerDay
        this.daysOfTheWeek = ["monday", "tuesday", "wednesday", "thursday", "friday"]
        this.activitySections = null
        this.activityStopTime = null
        this.hoursOfActivity = null
        this.timeTableObject = {}
    }

    estimateActivityStopTime() {
        // time should be measured im minutes
        let timeEstimateInMinutes = (this.numberOfClassesPerDay * this.classDuration) + this.longBreak + this.shortBreak
        let hrs = parseInt(timeEstimateInMinutes/60)
        let mins = timeEstimateInMinutes%60
        let estimateStop = this.activityStartTime + hrs
        this.hoursOfActivity = `${hrs}.${mins} hours`
        this.estimateActivityStopTime = `${estimateStop}:${mins}:00`
    }

    activitySectioning() {
        if (this.numberOfClassesPerDay < 3 || this.numberOfClassesPerDay > 10) {
            throw "Activity per day CANNOT be less than 3 or greater than 10"
        } else {
            if (this.numberOfClassesPerDay == 3) {
                this.activitySectioning = [1, 1, 1]
            } else if (this.numberOfClassesPerDay == 4) {
                this.activitySectioning = [2, 1, 1]
            } else if (this.numberOfClassesPerDay == 5) {
                this.activitySectioning = [2, 2, 1]
            } else if (this.numberOfClassesPerDay == 6) {
                this.activitySectioning = [3, 2, 1]
            } else if (this.numberOfClassesPerDay == 7) {
                this.activitySectioning = [3, 2, 2]
            } else if (this.numberOfClassesPerDay == 8) {
                this.activitySectioning = [3, 3, 2]
            } else if (this.numberOfClassesPerDay == 9) {
                this.activitySectioning = [4, 3, 2]
            } else if (this.numberOfClassesPerDay == 10) {
                this.activitySectioning = [5, 3, 2]
            }
        }
    }

    generateTimeTableDailyActivities() {
        this.estimateActivityStopTime()
        this.activitySectioning()
        let accumulatedTime = 0
        let activityCounter = 0
        let updateStart = ""

        const dailyActivity = []

        const activity = {
            id: null,
            label: "",
            startTime: null,
            endTime: null
        }

        let newActivity = null
        let esTime = null
        let result = null

        for (let i=0; i<this.activitySectioning[0]; i++) {
            accumulatedTime += this.classDuration
            activityCounter += 1
            result = this.computeOneActivityPerTime(accumulatedTime, activityCounter, updateStart)
            updateStart = result[1]
            dailyActivity.push(result[0])
        }

        // compute long break activity
        accumulatedTime += this.longBreak
        activityCounter += 1
        result = this.computeOneActivityPerTime(accumulatedTime, activityCounter, updateStart, "long_break")
        updateStart = result[1]
        dailyActivity.push(result[0])

        for (let i=0; i<this.activitySectioning[1]; i++) {
            accumulatedTime += this.classDuration
            activityCounter += 1
            result = this.computeOneActivityPerTime(accumulatedTime, activityCounter, updateStart)
            updateStart = result[1]
            dailyActivity.push(result[0])
        }

        // compute short break activity
        accumulatedTime += this.shortBreak
        activityCounter += 1
        result = this.computeOneActivityPerTime(accumulatedTime, activityCounter, updateStart, "short_break")
        updateStart = result[1]
        dailyActivity.push(result[0])


        for (let i=0; i<this.activitySectioning[2]; i++) {
            accumulatedTime += this.classDuration
            activityCounter += 1
            result = this.computeOneActivityPerTime(accumulatedTime, activityCounter, updateStart)
            updateStart = result[1]
            dailyActivity.push(result[0])
        }

        
        // Create timeTableObject
        for (let i=0; i<this.daysOfTheWeek.length; i++) {
            this.timeTableObject[this.daysOfTheWeek[i]] = dailyActivity
        }

        return {
            'timetable': this.timeTableObject,
            'closingHr': this.activityStopTime,
            'hrs': this.hoursOfActivity
        }
    }

    computeActivityEndTime(timeEstimate) {
        let hr = parseInt(timeEstimate / 60, 10)
        let mins = timeEstimate % 60
        return [
            hr, mins
        ]
    }

    computeOneActivityPerTime(accumulatedTime, activityCounter, startTimeUpdate, label=null) {
        const activity = {};
        let break_label = label === "long_break" ? "Long Break" : "Short Break"

        if (activityCounter === 1) {
            let estTime = this.computeActivityEndTime(accumulatedTime)
            let start = this.activityStartTime < 10 ? `0${this.activityStartTime}:00` : `${this.activityStartTime}:00`
            activity.activity_ref = `lesson_${activityCounter}`
            activity.label = `Lesson ${activityCounter}`
            activity.start = start
            let stopTime = estTime[0] + this.activityStartTime
            activity.end = stopTime < 10 ? `0${stopTime}:${ estTime[1] < 10 ? '0' + estTime[1] : estTime[1] }` : `${stopTime}:${ estTime[1] < 10 ? '0' + estTime[1] : estTime[1] }`
            activity.course_ref = ""
            startTimeUpdate = stopTime < 10 ? `0${stopTime}:${ estTime[1] < 10 ? '0' + estTime[1] : estTime[1] }` : `${stopTime}:${ estTime[1] < 10 ? '0' + estTime[1] : estTime[1] }`
        } else {
            let estTime = this.computeActivityEndTime(accumulatedTime)
            activity.activity_ref = label ? label : `lesson_${activityCounter}`
            activity.label = label ? break_label : `Lesson ${activityCounter}`
            activity.start = startTimeUpdate
            let stopTime = estTime[0] + this.activityStartTime
            activity.end = stopTime < 10 ? `0${stopTime}:${ estTime[1] < 10 ? '0' + estTime[1] : estTime[1] }` : `${stopTime}:${ estTime[1] < 10 ? '0' + estTime[1] : estTime[1] }`
            activity.course_ref = ""
            startTimeUpdate = stopTime < 10 ? `0${stopTime}:${ estTime[1] < 10 ? '0' + estTime[1] : estTime[1] }` : `${stopTime}:${ estTime[1] < 10 ? '0' + estTime[1] : estTime[1] }`
        }
        return [activity, startTimeUpdate]
    }
}

export default TimeTable