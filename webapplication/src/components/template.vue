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
                            <th style="width: 80%">Summary</th>
                            <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                            <td>1,001</td>
                            <td>
                                <div class="student-summary">
                                    <div class="row">
                                        <div class="col-12">
                                            <div class="row">
                                                <div class="col-8">
                                                    <h4>Ode Akugbe (saa/2020/001)</h4>
                                                </div>
                                                <div class="col-4 ">
                                                
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="row">
                                        <div class="col-12">
                                            <div class="row">
                                                <div class="col-md-3">Mobile: 07038929350</div> <div class="col-md-3">Grade: 3</div> <div class="col-md-3">Email: akugbeode@gmail.com</div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </td>
                            <td>ipsum</td>
                            </tr>
                        </tbody>
                        </table>
                    </div>
                    <div class="jumbotron">
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
        }
    }
</script>

<style scoped>
    
</style>