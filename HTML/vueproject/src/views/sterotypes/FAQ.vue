<template>
    <div>
        <el-container>
            <el-header class="help-header"><h1>Help Center</h1></el-header>
            <el-container>
                <el-aside width="250px">
                    <el-menu default-active="3-1" class="el-menu-vertical" mode="vertical">
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
                            <el-menu-item index="4-1">
                                <i class="el-icon-document"></i>
                                <span>FAQ</span>
                            </el-menu-item>
                            <el-menu-item index="4-2" v-on:click="goToLiveSupport">
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
                    <div v-if="activeIndex === '1'">
                        <h2>Frequently Asked Questions</h2>
                        <el-collapse>
                            <el-collapse-item title="How do I connect my bank accounts?" name="1">
                                <div>You can connect your bank accounts by navigating to the "Accounts" section and clicking on "Add New Account". Our AI-powered system supports integration with over 10,000 financial institutions worldwide.</div>
                            </el-collapse-item>
                            <el-collapse-item title="How does the AI analyze my spending habits?" name="2">
                                <div>Our AI analyzes your transaction history to identify patterns and categorize your expenses automatically. It learns from your spending behavior over time to provide personalized insights and recommendations for better financial management.</div>
                            </el-collapse-item>
                            <el-collapse-item title="Is my financial data secure?" name="3">
                                <div>Yes, we use bank-level encryption to protect your data. We never store your bank credentials, and all connections are made through secure APIs. Our AI processes your data locally whenever possible to ensure maximum privacy.</div>
                            </el-collapse-item>
                            <el-collapse-item title="How can I set up budget alerts?" name="4">
                                <div>Go to the "Budgets" section, create a new budget category or select an existing one, and enable "Smart Alerts". Our AI will monitor your spending and notify you when you're approaching your budget limits.</div>
                            </el-collapse-item>
                        </el-collapse>
                    </div>
                    <div v-else-if="activeIndex === '2'">
                        <h2>Documentation</h2>
                        <el-card v-for="(doc, index) in helpDocs" :key="index" class="doc-card">
                            <div slot="header">
                                <span>{{doc.title}}</span>
                            </div>
                            <div>{{doc.description}}</div>
                            <el-button type="text" style="margin-top: 10px">Read More</el-button>
                        </el-card>
                    </div>
                    <div v-else-if="activeIndex === '3'">
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
                    </div>
                    <div v-else-if="activeIndex === '4'">
                        <h2>Feedback</h2>
                        <el-form :model="feedbackForm" label-width="120px">
                            <el-form-item label="Feedback Type">
                                <el-select v-model="feedbackForm.type" placeholder="Select feedback type">
                                    <el-option label="Feature Request" value="feature"></el-option>
                                    <el-option label="Bug Report" value="bug"></el-option>
                                    <el-option label="AI Improvement" value="ai"></el-option>
                                    <el-option label="Other" value="other"></el-option>
                                </el-select>
                            </el-form-item>
                            <el-form-item label="Subject">
                                <el-input v-model="feedbackForm.title" placeholder="Enter a subject"></el-input>
                            </el-form-item>
                            <el-form-item label="Message">
                                <el-input type="textarea" v-model="feedbackForm.content" rows="5" placeholder="Please describe your feedback in detail"></el-input>
                            </el-form-item>
                            <el-form-item label="Contact Info">
                                <el-input v-model="feedbackForm.contact" placeholder="Leave your contact information for us to follow up"></el-input>
                            </el-form-item>
                            <el-form-item>
                                <el-button type="primary" @click="submitFeedback">Submit Feedback</el-button>
                            </el-form-item>
                        </el-form>
                    </div>
                </el-main>
            </el-container>
        </el-container>
    </div>
</template>

<script>
export default {
    name: 'HelpCenter',
    data() {
        return {
            activeIndex: '1',
            helpDocs: [
                {
                    title: 'Getting Started Guide',
                    description: 'Learn how to set up your AI-powered finance tracker and connect your first accounts.'
                },
                {
                    title: 'AI Features Explained',
                    description: 'Discover how our AI analyzes your finances and provides personalized insights to improve your financial health.'
                },
                {
                    title: 'Budget Management',
                    description: 'Master the art of creating smart budgets with AI-powered recommendations based on your spending patterns.'
                },
                {
                    title: 'Financial Goal Setting',
                    description: 'Learn how to use our AI to set achievable financial goals and track your progress over time.'
                }
            ],
            feedbackForm: {
                type: '',
                title: '',
                content: '',
                contact: ''
            }
        }
    },
    methods: {
        handleSelect(key) {
            this.activeIndex = key;
        },
        submitFeedback() {
            // Logic for submitting feedback
            this.$message({
                message: 'Thank you for your feedback! We will review it shortly.',
                type: 'success'
            });
            // Reset form
            this.feedbackForm = {
                type: '',
                title: '',
                content: '',
                contact: ''
            };
        },
        handleFAQClick() {
            this.activeIndex = '1';
        },
        goToLiveSupport() {
            this.$router.push('/livesupport');
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