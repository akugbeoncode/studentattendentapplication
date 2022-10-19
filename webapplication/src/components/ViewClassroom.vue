<template>
    <div class="view-classroom">
        <div class="container-fluid">
            <div class="row">
                <admin-sidebar class="admin-sidebar"></admin-sidebar>
                <main role="main" class="col-md-9 ml-sm-auto col-lg-10 px-md-4">
                    <inner-header :pageHeader="pageHeader" class="inner-header"></inner-header>
                    <table class="table table-bordered">
                        <tbody>
                            <tr>
                                <th style="width: 250px" scope="row">Capacity</th>
                                <td>{{capacity}}</td>
                            </tr>                   
                        </tbody>
                    </table>
                    <div class="row">
                        <router-link class="btn btn-secondary ml-3" to="/admin-dashboard/classroom-manager">Back</router-link>
                        <router-link 
                            class="btn btn-warning ml-3" 
                            :to="{
                                name: 'edit-classroom',
                                params: { classroom_ref: classroom_ref }
                            }"
                        >
                            Edit
                        </router-link>
                        <button @click="deleteClassroomRecord" class="btn btn-danger ml-3">Delete</button>
                    </div>
                    <h4 class="h3 mt-5">Assign Student to this Classroom</h4>
                    <hr>
                    <div class="row">
                        <div class="col-md-9 mb-3">
                            <select v-model="selectedStudentRef" class="form-control">
                                <option value="">Select Prospective Student</option>
                                <option 
                                    v-for="student in students"
                                    :key="student.student_ref"
                                    :value="student.student_ref"
                                >
                                    {{student.fullname}} ({{student.student_ref}})
                                </option>
                            </select>
                        </div>
                        <div class="col-md-3 mb-3">
                            <button :disabled="selectedStudentRef === '' || loading" @click="addStudentToThisClassroom" class="btn btn-success" type="button">
                                Add Student <i v-if="loading" class="fa fa-spinner fa-pulse fa-fw"></i><span v-if="loading" class="sr-only">Loading...</span>
                            </button>
                        </div>
                    </div>
                    <h4 class="h3 mt-5">Time Table</h4>
                    <hr>
                    <div v-if="isEmpty(timetable)" class="row mt-5">
                        <router-link 
                            class="btn btn-success btn-lg" 
                            :to="{
                                name: 'create-timetable',
                                params: {classroom_ref: classroom_ref}
                            }"
                        >
                            Create Time Table
                        </router-link>
                    </div>
                    <div v-else class="table-responsive">
                        <table class="table table-striped table-sm">
                            <thead>
                                <tr>
                                    <th class="pt-4 pb-3">Week Day</th>
                                    <th class="pt-4 pb-3 text-centered"
                                        v-for="activity in timetable['timetable']['monday']"
                                        :key="activity.activity_ref"
                                    >{{activity.start}} - {{activity.end}}</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="(day, index) in weekDays" :key="index">
                                    <th class="pt-4 pb-4" style="text-transform: capitalize;" scope="row">{{day}}</th>
                                    <td class="pt-4 pb-4"
                                        v-for="activity in timetable.timetable[day]"
                                        :key="activity.activity_ref"
                                    >
                                        <div class="select-container"  v-if="getVisibilityStatus(activity.activity_ref)">
                                            <input readonly class="break-labels form-control" type="text" :value="activity.course_ref == '' ? 'Not Set' : getCourseByRef(activity.course_ref)">
                                        </div>
                                        <div class="select-container" v-else>
                                            <input readonly class="break-labels form-control" type="text" :value="activity.label">
                                        </div>
                                    </td>
                                </tr> 
                            </tbody>
                        </table>
                    </div>
                    <div v-if="!isEmpty(timetable)" class="row mt-5">
                        <router-link 
                            class="btn btn-warning btn-lg" 
                            :to="{
                                name: 'edit-timetable',
                                params: { classroom_ref: classroom_ref }
                            }"
                        >
                            Edit
                        </router-link>
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

    export default {
        name: 'view-classroom',
        components: {
            'admin-sidebar': AdminSideBar,
            'inner-header': InnerHeader
        },
        data () {
            return {
                classroom_ref: "",
                label: '',
                code: '',
                capacity: null,
                weekDays: ["monday", "tuesday", "wednesday", "thursday", "friday"],
                timetable: null,
                courses: [],
                students: [],
                classroom_members: [],
                prospects: [],

                selectedStudentRef: "",
                loading: false
            }
        },

        beforeRouteEnter(to, from, next) {
            db.collection("classrooms").where('classroom_ref', '==', to.params.classroom_ref).get()
                .then( querySnapshot => {
                    querySnapshot.forEach( doc => {
                        next(vm => {
                            vm.classroom_ref = doc.data().classroom_ref
                            vm.label = doc.data().label
                            vm.code = doc.data().code
                            vm.capacity = doc.data().capacity
                        })
                    })
                })
        }, 

        watch: {
            '$route': 'fetchData'
        }, 

        computed: {
            pageHeader() {
                return this.label + " (" + this.code + ")"
            }
        },

        methods: {
            fetchData() {
                db.collection("classrooms").where('classroom_ref', '==', this.$route.params.classroom_ref).get()
                    .then( querySnapshot => {
                        querySnapshot.forEach( doc => {
                            this.classroom_ref = doc.data().classroom_ref
                            this.label = doc.data().label
                            this.code = doc.data().code
                            this.capacity = doc.data().capacity
                        })
                    })
            },

            getVisibilityStatus(activity_ref) {
                if (activity_ref === 'long_break' || activity_ref === 'short_break') {
                    return false
                }
                return true
            },

            getCourseByRef(course_ref) {
                let selected = null
                this.courses.map( course => {
                    if (course.course_ref === course_ref) {
                        selected = course
                    }
                })

                if (selected) {
                    return selected.title
                } else {
                    return "Not Set"
                }
            },

            deleteClassroomRecord() {
                if (confirm("Are you sure?")) {
                    db.collection("classrooms").where('classroom_ref', '==', this.classroom_ref).get()
                        .then( querySnapshot => {
                            querySnapshot.forEach( doc => {
                                doc.ref.delete()
                                this.$router.push("/admin-dashboard/classroom-manager")
                            })
                        })
                }
            },

            isEmpty(obj){
                for(var key in obj) {
                    if(obj.hasOwnProperty(key))
                        return false;
                }
                return true;
            },

            addStudentToThisClassroom() {
                let canAdd = true
                this.classroom_members.map(member => {
                    if (member.student_ref === this.selectedStudentRef) {
                        canAdd = false
                    }
                })

                if (canAdd) {
                    this.loading = true
                    db.collection("classroom_members").add({
                        "classroom_ref":  this.classroom_ref,
                        "student_ref": this.selectedStudentRef
                    }).then( docRef => {
                        this.selectedStudentRef = ""
                        this.loading = false
                        alert("Student successfully added to this classroom ")
                        location.reload()
                    })
                    .catch( error => {
                        console.log(error)
                        this.loading = false 
                    })
                } else {
                    alert("Student is already a member of classroom. Student cannot belong to two class rooms")
                    this.selectedStudentRef = ""
                }
            }
        },

        created() {
            db.collection("time_tables").where('classroom_ref', '==', this.$route.params.classroom_ref).get()
                .then( querySnapshot => {
                    querySnapshot.forEach( doc => {
                        this.timetable = doc.data().timetable
                    })
                })

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

            db.collection("students").orderBy("fullname").get().then(querySnapshot => {
                querySnapshot.forEach(doc => {
                    const data = {
                        "id": doc.id,
                        "student_ref":  doc.data().student_ref,
                        "fullname": doc.data().fullname,
                        "email": doc.data().email,
                        "grade": doc.data().grade,
                        "guardian": doc.data().guardian,
                        "guardian_mobile": doc.data().guardian_mobile,
                        "mobile": doc.data().mobile
                    }

                    this.students.push(data)
                })
            })

            db.collection("classroom_members").get().then(querySnapshot => {
                querySnapshot.forEach(doc => {
                    const data = {
                        "id": doc.id,
                        "student_ref":  doc.data().student_ref,
                        "classroom_ref": doc.data().classroom_ref,
                    }

                    this.classroom_members.push(data)
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

    td {
        border: 1px solid #aaa;
    }
</style>