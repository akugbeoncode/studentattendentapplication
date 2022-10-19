<template>
    <div class="dashboard">
        <div class="container-fluid">
            <div class="row">
                <admin-sidebar class="admin-sidebar"></admin-sidebar>
                <main role="main" class="col-md-9 ml-sm-auto col-lg-10 px-md-4">
                    <inner-header :pageHeader="pageHeader" class="inner-header"></inner-header>
                    <div class="center-content pt-5">
                        <h2 class="h2" style="text-align: center;">Welcome Student Attendant Application</h2>
                        <h1 style="text-align: center; margin: 15% 0">This application is undergoing contruction </h1>
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
        name: 'dashboard',
        components: {
            'admin-sidebar': AdminSideBar,
            'inner-header': InnerHeader
        },
        data () {
            return {
               pageHeader: "Dashboard"
            }
        }
    }
</script>

<style scoped>
    .center-content {
        width: 80%;
        height: 60vh;

        margin: 10vh auto;
        background: #eee;
    }
</style>