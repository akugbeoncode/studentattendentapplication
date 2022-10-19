<template>
    <div class="student-Manager">
        <div class="container-fluid">
            <div class="row">
                <admin-sidebar class="admin-sidebar"></admin-sidebar>
                <main role="main" class="col-md-9 ml-sm-auto col-lg-10 px-md-4">
                    <inner-header :pageHeader="pageHeader" class="inner-header"></inner-header>
                    <div v-if="students.length > 0" class="table-responsive">
                        <table class="table table-striped table-sm">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th style="width: 90%">Summary</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr 
                                v-for="(student, index) in students"
                                :key="student.id"
                            >
                                <td class="centered-count">{{index+1}}</td>
                                <td>
                                    <router-link
                                        class="ref-link"
                                        :to="{
                                            name: 'view-student', 
                                            params: { student_ref: student.student_ref}
                                        }"
                                    >
                                        <div class="student-summary mt-3 mb-3">
                                            <div class="row">
                                                <div class="col-12">
                                                    <div class="row">
                                                        <div class="col-8">
                                                            <h4>{{student.fullname}} ({{student.student_ref}})</h4>
                                                        </div>
                                                        <div class="col-4 ">
                                                        
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-12">
                                                    <div class="row">
                                                        <div class="col-md-3">Mobile: {{student.mobile}}</div> 
                                                        <div class="col-md-3">Grade: {{student.grade}}</div> 
                                                        <div class="col-md-3">Email: {{student.email}}</div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </router-link>
                                </td>
                            </tr>
                        </tbody>
                        </table>
                    </div>
                    <div v-else class="jumbotron">
                        <h4 style="text-align: center;">No Student Record Found.</h4>
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
        name: 'student-Manager',
        components: {
            'admin-sidebar': AdminSideBar,
            'inner-header': InnerHeader
        },
        data () {
            return {
               pageHeader: "Student Manager",
               students: []
            }
        },

        created() {
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
        }
    }
</script>

<style scoped>
    .centered-count {
        height: 80px;
        display: flex;
        align-items: center;
        justify-content: center;

        font-weight: 900;
    }

    .ref-link {
        text-decoration: none;
        color: #000;
    }

    .ref-link:hover {
        color: #888;
    }
</style>