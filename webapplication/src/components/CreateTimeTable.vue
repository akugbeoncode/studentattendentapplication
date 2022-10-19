<template>
    <div class="create-timetable">
         <div class="container-fluid">
            <div class="row">
                <admin-sidebar class="admin-sidebar"></admin-sidebar>
                <main role="main" class="col-md-9 ml-sm-auto col-lg-10 px-md-4">
                    <inner-header :pageHeader="pageHeader" class="inner-header"></inner-header>
                    <div v-if="isEmpty(timetable)" class="col-md-6 order-md-1 offset-md-3">
                        <form>
                            <div class="mb-3">
                                <label>Class Start Hour</label>
                                <select v-model="activityStartTime" class="form-control" required>
                                    <option v-for="(n, index) in 24" :key="index" :value="index">{{index}}</option>
                                </select>
                            </div>

                            <div class="mb-3">
                                <label for="email">Class Duration (In Minutes)</label>
                                <input type="number" v-model="activityDuration" class="form-control" placeholder="45" required>
                            </div>

                            <div class="mb-3">
                                <label for="address2">Classes Per Day (greater than 2 and less than 11)</label>
                                <input type="number" v-model="numberOfActivityPerDay" class="form-control" placeholder="7" required>
                            </div>

                            <div class="mb-3">
                                <label for="address2">Long Break Duration (In Minutes)</label>
                                <input type="number" v-model="longBreak" class="form-control" placeholder="20" required>
                            </div>

                            <div class="mb-3">
                                <label for="address2">Short Break Duration (In Minutes)</label>
                                <input type="number" v-model="shortBreak" class="form-control" placeholder="15" required>
                            </div>

                            <hr class="mb-4">
                            <button  :disabled="loading" @click="generateTimeTableObject" class="btn btn-primary btn-lg btn-block" type="button">
                                Generate Time Table <i v-if="loading" class="fa fa-spinner fa-pulse fa-fw"></i><span v-if="loading" class="sr-only">Loading...</span>
                            </button>
                        </form>
                    </div>
                    <div v-else class="container-fluid">
                        <div class="row">
                            <div class="col-md-6 offset-md-3">
                                <div class="mb-3">
                                    <label for="email">Time Table Ref</label>
                                    <input disabled type="text" v-model="timetable_ref" class="form-control" required>
                                </div>

                                <div class="mb-3">
                                    <label for="address2">Classroom Ref</label>
                                    <input disabled type="text" v-model="classroom_ref" class="form-control" required>
                                </div>
                            </div>
                        </div>
                        <h4 class="h1 mt-5" style="text-align: center;">Time Table</h4>
                        <div class="table-responsive">
                            <table class="table table-striped table-sm">
                            <thead>
                                <tr>
                                    <th class="pt-4 pb-3">Week Day</th>
                                    <th class="pt-4 pb-3 text-centered"
                                        v-for="activity in timetable.timetable[weekDays[0]]"
                                        :key="activity.activity_ref"
                                    >{{activity.start}} - {{activity.end}}</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="(day, index) in weekDays" :key="index">
                                    <th class="pt-5 pb-5" style="text-transform: capitalize;" scope="row">{{day}}</th>
                                    <td class="pt-5 pb-5"
                                        v-for="activity in timetable.timetable[day]"
                                        :key="activity.activity_ref"
                                    >
                                        <div class="select-container"  v-if="getVisibilityStatus(activity.activity_ref)">
                                            <select class="form-control" v-model="activity.course_ref" v-html="options"></select>
                                        </div>
                                        <div class="select-container" v-else>
                                            <input readonly class="break-labels form-control" type="text" :value="activity.label">
                                        </div>
                                    </td>
                                </tr> 
                            </tbody>
                            </table>
                        </div>
                        <button @click="cancel" class="btn btn-danger btn-lg m-2">Cancel</button>
                        <button @click="reset" class="btn btn-secondary btn-lg m-2">Reset</button>
                        <button @click="save" class="btn btn-success btn-lg m-2">Save <i v-if="loading" class="fa fa-spinner fa-pulse fa-fw"></i></button>
                    </div>
                </main>
            </div>
        </div>
    </div>
</template>

<script>

    import db from '../firebase/init'
    import AdminSideBar from './AdminSidebar'
    import InnerHeader from './InnerHeader'

    import TimeTable from '../helpers/TimeTable'

    export default {
        name: 'create-timetable',
        components: {
            'admin-sidebar': AdminSideBar,
            'inner-header': InnerHeader
        },
        data () {
            return {
               pageHeader: "Classroom Manager/Create Timetable",
               timetable_ref: '',
               classroom_ref: '',
               weekDays: ["monday", "tuesday", "wednesday", "thursday", "friday"],
               timetable: {},
               activityStartTime: 8,
               activityDuration: 0,
               numberOfActivityPerDay: 0,
               longBreak: 0,
               shortBreak: 0,
               courses: [],
               canSave: false,

               loading: false
            }
        },

        computed: {
            options() {
                let options = `<option value=""> -- Select Course -- </option>`
                this.courses.map( course => {
                    options += `<option value="${course.course_ref}">${course.title} (${course.course_ref})</option>`
                })
                return options;
            }
        },

        methods: {
            generateTimeTableObject() {
                this.loading = true
                if (this.activityStartTime && this.activityDuration && this.numberOfActivityPerDay && this.longBreak && this.shortBreak) {
                    const timetable = new TimeTable(
                        parseInt(this.activityStartTime, 10),
                        parseInt(this.activityDuration, 10),
                        parseInt(this.longBreak, 10),
                        parseInt(this.shortBreak, 10),
                        parseInt(this.numberOfActivityPerDay, 10)
                    )
                    const generatedTimeTable = timetable.generateTimeTableDailyActivities()
                    localStorage.setItem("GENERATED_TIMETABLE", JSON.stringify(generatedTimeTable))
                    this.timetable = generatedTimeTable
                    this.loading = false
                } else {
                    alert("Please complete all fields as required!!!")
                    this.loading = false
                }
            },

            isEmpty(obj){
                for(var key in obj) {
                    if(obj.hasOwnProperty(key))
                        return false;
                }
                return true;
            },

            cancel() {
                localStorage.setItem("GENERATED_TIMETABLE", "")
                this.$router.push({
                    name: "view-classroom",
                    params: {classroom_ref: this.$route.params.classroom_ref}
                })
            },

            reset() {
                Object.keys(this.timetable.timetable).forEach( day => {
                    this.timetable.timetable[day].map( activity => {
                        activity.course_ref = ""
                    })
                })
            },

            save() {
                this.loading = true
                 db.collection("time_tables").add({
                    "timetable_ref": this.timetable_ref,
                    "classroom_ref": this.classroom_ref,
                    "timetable": this.timetable,
                }).then( docRef => { 
                    alert("Time Table Created Successfully!!!")
                    this.loading = false
                    this.$router.push({ 
                        name: 'view-classroom',
                        params: { classroom_ref: this.classroom_ref}
                    })
                })
                .catch( error => {
                    this.loading = false
                    console.log(error)
                })
            },

            getVisibilityStatus(activity_ref) {
                if (activity_ref === 'long_break' || activity_ref === 'short_break') {
                    return false
                }
                return true
            },

            generateRef() {
                let length = 10,
                    charset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789@#$%&",
                    token = "";
                for (let i = 0, n = charset.length; i < length; ++i) {
                    token += charset.charAt(Math.floor(Math.random() * n));
                }
                return token;
            }
        },

        created() {
            this.classroom_ref = this.$route.params.classroom_ref
            this.timetable_ref = this.generateRef()

            if (localStorage.getItem("GENERATED_TIMETABLE")) {
                this.timetable = JSON.parse(localStorage.getItem("GENERATED_TIMETABLE"))
            }

            db.collection("courses").orderBy("title").get().then(querySnapshot => {
                querySnapshot.forEach(doc => {
                    const data = {
                        "id": doc.id,
                        "course_ref": doc.data().course_ref,
                        "title": doc.data().title,
                        "code": doc.data().code,
                        "taught_by": doc.data().taught_by
                    }

                    this.courses.push(data)
                })
            })
        }
    }
</script>

<style scoped>
    .break-labels {
        border: none;
        font-weight: 900;
        text-align: center;
        background: transparent;
    }

    .select-container {
        width: 250px !important
    }

    .text-centered {
        text-align: center;
        outline: none;
    }
</style>