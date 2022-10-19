<template>
    <div class="classroom-manager">
        <div class="container-fluid">
            <div class="row">
                <admin-sidebar class="admin-sidebar"></admin-sidebar>
                <main role="main" class="col-md-9 ml-sm-auto col-lg-10 px-md-4">
                    <inner-header :pageHeader="pageHeader" class="inner-header"></inner-header>
                    <div v-if="classrooms.length > 0" class="table-responsive">
                        <table class="table table-striped table-sm">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th style="width: 90%">Summary</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr 
                                v-for="(classroom, index) in classrooms"
                                :key="classroom.id"
                            >
                                <td class="centered-count">{{index+1}}</td>
                                <td>
                                    <router-link
                                        class="ref-link"
                                        :to="{
                                            name: 'view-classroom', 
                                            params: { classroom_ref: classroom.classroom_ref}
                                        }"
                                    >
                                        <div class="student-summary mt-3 mb-3">
                                            <div class="row">
                                                <div class="col-12">
                                                    <div class="row">
                                                        <div class="col-8">
                                                            <h4>{{classroom.label}} ({{classroom.code}})</h4>
                                                        </div>
                                                        <div class="col-4 ">
                                                        
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="row">
                                                <div class="col-12">
                                                    <div class="row">
                                                        <div class="col-md-3">Capacity: {{classroom.capacity}}</div> 
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
                        <h4 style="text-align: center;">No Classroom Record Found.</h4>
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
        name: 'classroom-manager',
        components: {
            'admin-sidebar': AdminSideBar,
            'inner-header': InnerHeader
        },
        data () {
            return {
               pageHeader: "Classroom Manager",
               classrooms: []
            }
        },

        created() {
            db.collection("classrooms").orderBy("label").get().then(querySnapshot => {
                querySnapshot.forEach(doc => {
                    const data = {
                        "id": doc.id,
                        "classroom_ref":  doc.data().classroom_ref,
                        "label": doc.data().label,
                        "code": doc.data().code,
                        "capacity": doc.data().capacity
                    }

                    this.classrooms.push(data)
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