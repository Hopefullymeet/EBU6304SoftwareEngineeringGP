<template>
    <div>
        <el-container>
            <el-header class="help-header"><h1>Help Center</h1></el-header>
            <el-container>
                <el-aside width="250px">
                    <el-menu default-active="4-2" class="el-menu-vertical" mode="vertical">
                        <el-menu-item index="1">
                            <i class="el-icon-wallet"></i>
                            <span>Account</span>
                        </el-menu-item>
                        <el-menu-item index="2">
                            <i class="el-icon-tickets"></i>
                            <span>Billing & Subscriptions</span>
                        </el-menu-item>
                        <el-menu-item index="3">
                            <i class="el-icon-money"></i>
                            <span>Budget</span>
                        </el-menu-item>
                        <el-submenu index="4">
                            <template slot="title">
                                <i class="el-icon-question"></i>
                                <span>Help Center</span>
                            </template>
                            <el-menu-item index="4-1" v-on:click="goToFAQ">
                                <i class="el-icon-document"></i>
                                <span>FAQ</span>
                            </el-menu-item>
                            <el-menu-item index="4-2">
                                <i class="el-icon-service"></i>
                                <span>Live Support</span>
                            </el-menu-item>
                            <el-menu-item index="4-3" v-on:click="goToFeedback">
                                <i class="el-icon-chat-line-square"></i>
                                <span>Feedback</span>
                            </el-menu-item>
                        </el-submenu>
                    </el-menu>
                </el-aside>
                <el-main>
                    <h2>Live Support</h2>
                    <el-card>
                        <div>
                            <p>Our support team is ready to assist you with any questions about your AI-powered finance tracker.</p>
                            <p>Support Hours: Monday to Friday, 9:00 AM - 6:00 PM EST</p>
                            <el-button type="primary" icon="el-icon-service">Start Chat</el-button>
                            <div style="margin-top: 20px">
                                <p>You can also reach us through:</p>
                                <p>Email: support@aifinancetracker.com</p>
                                <p>Phone: 1-800-123-4567</p>
                            </div>
                        </div>
                    </el-card>
                </el-main>
            </el-container>
        </el-container>
    </div>
</template>

<script>
export default {
    name: 'LiveSupportView',
    methods: {
        goToFAQ() {
            this.$router.push('/faq');
        },
        goToFeedback() {
            this.$router.push('/feedback');
        }
    }
}
</script>   

<style scoped>
.el-header {
    background: linear-gradient(135deg, #3a8ee6, #5ca1ff);
    color: white;
    text-align: center;
    line-height: 60px;
    padding: 0;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.help-header h1 {
    margin: 0;
    font-weight: 500;
    font-size: 24px;
    letter-spacing: 1px;
}

.el-aside {
    background-color: #f8f8f8;
    border-right: 1px solid #e6e6e6;
    min-width: 250px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.el-menu-vertical {
    border-right: none;
    width: 100%;
}

.el-menu-item span {
    white-space: normal;
    line-height: 1.2;
    padding: 10px 0;
}

.el-submenu .el-menu-item span {
    white-space: normal;
    line-height: 1.2;
}

.doc-card {
    margin-bottom: 15px;
    transition: all 0.3s;
    border-radius: 8px;
}

.doc-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.el-main {
    padding: 20px 30px;
}

.el-collapse-item {
    margin-bottom: 10px;
    border-radius: 4px;
    overflow: hidden;
}
</style> 