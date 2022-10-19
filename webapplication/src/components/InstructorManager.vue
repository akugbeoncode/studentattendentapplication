<template>
    <div class="instructor-manager">
        <div class="container-fluid">
            <div class="row">
                <admin-sidebar class="admin-sidebar"></admin-sidebar>
                <main role="main" class="col-md-9 ml-sm-auto col-lg-10 px-md-4">
                    <inner-header :pageHeader="pageHeader" class="inner-header"></inner-header>
                    <div v-if="instructors.length > 0" class="table-responsive">
                        <table class="table table-striped table-sm">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th style="width: 90%">Summary</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr 
                                v-for="(instructor, index) in instructors"
                                :key="instructor.id"
                            >
                                <td class="centered-count">{{index+1}}</td>
                                <td>
                                    <router-link
                                        class="ref-link"
                                        :to="{
                                            name: 'view-instructor', 
                                            params: { instructor_ref: instructor.instructor_ref}
                                        }"
                                    >
                                        <div class="student-summary mt-3 mb-3">
                                            <div class="row">
                                                <div class="col-12">
                                                    <div class="row">
                                                        <div class="col-8">
                                                            <h4>{{instructor.fullname}} ({{instructor.instructor_ref}})</h4>
                                                        </div>
                                                        <div class="col-4 ">
                                                        
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-12">
                                                    <div class="row">
                                                        <div class="col-md-3">Mobile: {{instructor.mobile}}</div> 
                                                        <div class="col-md-3">Email: {{instructor.email}}</div>
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
                        <h4 style="text-align: center;">No Instructor Record Found.</h4>
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
        name: 'instructor-manager',
        components: {
            'admin-sidebar': AdminSideBar,
            'inner-header': InnerHeader
        },
        data () {
            return {
               pageHeader: "Instructor Manager",
               instructors: []
            }
        },

        created() {
            db.collection("instructors").orderBy("fullname").get().then(querySnapshot => {
                querySnapshot.forEach(doc => {
                    const data = {
                        "id": doc.id,
                        "instructor_ref":  doc.data().instructor_ref,
                        "fullname": doc.data().fullname,
                        "email": doc.data().email,
                        "device_ref": doc.data().device_ref,
                        "mobile": doc.data().mobile
                    }

                    this.instructors.push(data)
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