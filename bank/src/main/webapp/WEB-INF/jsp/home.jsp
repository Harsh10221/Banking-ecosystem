<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>NexGen NetBanking | Secure Banking</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');
        
        :root {
            --kotak-red: #ED1C24;
            --kotak-blue: #003366;
            --bg-gray: #F4F7FA;
        }

        body {
            font-family: 'Inter', sans-serif;
            background-color: var(--bg-gray);
        }

        .sidebar-link.active {
            background-color: rgba(237, 28, 36, 0.1);
            color: var(--kotak-red);
            border-right: 4px solid var(--kotak-red);
        }

        .card-shadow {
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
        }

        .btn-primary {
            background-color: var(--kotak-red);
            color: white;
            transition: all 0.3s;
        }

        .btn-primary:hover {
            background-color: #c4141a;
        }

        .gradient-header {
            background: linear-gradient(135deg, #003366 0%, #001a33 100%);
        }

        /* Hide scrollbar for Chrome, Safari and Opera */
        .no-scrollbar::-webkit-scrollbar {
            display: none;
        }

        /* Hide scrollbar for IE, Edge and Firefox */
        .no-scrollbar {
            -ms-overflow-style: none;
            scrollbar-width: none;
        }

        .tab-btn.active {
            border-bottom: 3px solid var(--kotak-red);
            color: var(--kotak-red);
            font-weight: 600;
        }

        /* Mobile Sidebar Animation */
        #sidebar {
            transition: transform 0.3s ease-in-out;
        }
        
        @media (max-width: 768px) {
            #sidebar.hidden-mobile {
                transform: translateX(-100%);
            }
            #sidebar.show-mobile {
                transform: translateX(0);
            }
        }
    </style>
</head>
<body class="h-screen flex overflow-hidden">

    <!-- Backdrop for Mobile Sidebar -->
    <div id="sidebar-backdrop" onclick="toggleMobileMenu()" class="fixed inset-0 bg-black/50 z-40 hidden md:hidden"></div>

    <!-- Sidebar -->
    <aside id="sidebar" class="fixed inset-y-0 left-0 w-64 bg-white border-r z-50 flex flex-col flex-shrink-0 md:relative md:translate-x-0 hidden-mobile">
        <div class="p-6 flex items-center justify-between">
            <div class="flex items-center gap-2">
                <div class="w-8 h-8 bg-red-600 rounded flex items-center justify-center text-white font-bold">NG</div>
                <span class="text-xl font-bold text-slate-800 tracking-tight">NexGen<span class="text-red-600">Bank</span></span>
            </div>
            <button onclick="toggleMobileMenu()" class="md:hidden text-gray-400 hover:text-red-600">
                <i class="fa-solid fa-xmark text-xl"></i>
            </button>
        </div>
        
        <nav class="flex-1 overflow-y-auto mt-4 px-4 space-y-1 no-scrollbar">
            <a href="#" onclick="showPage('home')" class="sidebar-link active flex items-center gap-3 p-3 rounded-lg text-slate-600 hover:bg-gray-50 transition-all" id="nav-home">
                <i class="fa-solid fa-house w-5"></i> <span>Home</span>
            </a>
            <a href="#" onclick="showPage('accounts')" class="sidebar-link flex items-center gap-3 p-3 rounded-lg text-slate-600 hover:bg-gray-50 transition-all" id="nav-accounts">
                <i class="fa-solid fa-building-columns w-5"></i> <span>Accounts & Deposits</span>
            </a>
            <a href="#" onclick="showPage('transfer')" class="sidebar-link flex items-center gap-3 p-3 rounded-lg text-slate-600 hover:bg-gray-50 transition-all" id="nav-transfer">
                <i class="fa-solid fa-paper-plane w-5"></i> <span>Fund Transfer</span>
            </a>
            <a href="#" onclick="showPage('statement')" class="sidebar-link flex items-center gap-3 p-3 rounded-lg text-slate-600 hover:bg-gray-50 transition-all" id="nav-statement">
                <i class="fa-solid fa-file-lines w-5"></i> <span>Statements</span>
            </a>
            <a href="#" onclick="showPage('loans')" class="sidebar-link flex items-center gap-3 p-3 rounded-lg text-slate-600 hover:bg-gray-50 transition-all" id="nav-loans">
                <i class="fa-solid fa-hand-holding-dollar w-5"></i> <span>Loans</span>
            </a>
            <a href="#" onclick="showPage('cards')" class="sidebar-link flex items-center gap-3 p-3 rounded-lg text-slate-600 hover:bg-gray-50 transition-all" id="nav-cards">
                <i class="fa-solid fa-credit-card w-5"></i> <span>Cards</span>
            </a>
            <a href="#" onclick="showPage('settings')" class="sidebar-link flex items-center gap-3 p-3 rounded-lg text-slate-600 hover:bg-gray-50 transition-all" id="nav-settings">
                <i class="fa-solid fa-gear w-5"></i> <span>Settings</span>
            </a>
            <a href="#" onclick="showPage('profile')" class="sidebar-link flex items-center gap-3 p-3 rounded-lg text-slate-600 hover:bg-gray-50 transition-all" id="nav-profile">
                <i class="fa-solid fa-user w-5"></i> <span>Profile</span>
            </a>
        </nav>

        <div class="p-6 border-t mt-auto text-xs text-gray-400">
            © 2025 NexGen Banking Ltd. <br> Secure 256-bit SSL
        </div>
    </aside>

    <!-- Main Content -->
    <main class="flex-1 flex flex-col min-w-0 overflow-hidden">
        <!-- Header -->
        <header class="h-16 bg-white border-b flex items-center justify-between px-4 sm:px-8 z-10 flex-shrink-0">
            <div class="flex items-center gap-3 sm:gap-4">
                <button onclick="toggleMobileMenu()" class="md:hidden text-gray-600 text-xl p-2 rounded hover:bg-gray-100">
                    <i class="fa-solid fa-bars"></i>
                </button>
                <h2 id="page-title" class="text-base sm:text-xl font-semibold text-slate-800 truncate max-w-[150px] sm:max-w-none">Welcome, ${user.fullName}!</h2>
            </div>
            <div class="flex items-center gap-3 sm:gap-6">
                <div class="relative hidden lg:block">
                    <input type="text" placeholder="Search features..." class="pl-10 pr-4 py-2 rounded-full border bg-gray-50 text-sm focus:outline-none focus:ring-1 focus:ring-red-500 w-64">
                    <i class="fa-solid fa-search absolute left-4 top-1/2 -translate-y-1/2 text-gray-400"></i>
                </div>
                <div class="flex items-center gap-3 sm:gap-4 border-l pl-3 sm:pl-6">
                    <button class="text-gray-500 relative"><i class="fa-regular fa-bell text-xl"></i><span class="absolute -top-1 -right-1 bg-red-600 w-2 h-2 rounded-full"></span></button>
                    <div class="w-8 h-8 rounded-full bg-slate-200 overflow-hidden cursor-pointer" onclick="showPage('profile')">
                        <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=Rajesh" alt="avatar">
                    </div>
                    <a href="index.html"><button class="text-red-600 font-medium text-xs sm:text-sm hidden sm:block">Logout</button></a>
                </div>
            </div>
        </header>

        <!-- Dynamic Content Area -->
        <div id="content-container" class="flex-1 overflow-y-auto p-4 sm:p-8 no-scrollbar">
            
            <!-- HOME PAGE -->
            <section id="page-home" class="page-section space-y-6">
                <!-- Summary Section -->
                <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
                    <div class="lg:col-span-2 gradient-header p-6 rounded-2xl text-white shadow-lg relative overflow-hidden">
                        <div class="relative z-10">
                            <h3 class="text-sm sm:text-lg opacity-80 mb-1">Total Balance</h3>
                            <div class="text-2xl sm:text-4xl font-bold mb-6">₹ ${user.accountDetails.balance}</div>
                            <!-- <div class="grid grid-cols-2 gap-4 border-t border-white/10 pt-4">
                                <div>
                                    <p class="text-[10px] sm:text-xs opacity-70">Total Assets</p>
                                    <p class="text-sm sm:text-lg font-semibold text-green-400">₹ 22,34,000</p>
                                </div>
                                <div>
                                    <p class="text-[10px] sm:text-xs opacity-70">Total Liabilities</p>
                                    <p class="text-sm sm:text-lg font-semibold text-red-400">₹ 7,51,499</p>
                                </div>
                            </div> -->
                        </div>
                        <i class="fa-solid fa-shield-halved absolute -right-4 -bottom-4 text-white/5 text-[150px] sm:text-[200px] pointer-events-none"></i>
                    </div>

                    <div class="bg-white p-6 rounded-2xl card-shadow border">
                        <h3 class="font-bold text-slate-800 mb-4 flex items-center justify-between">
                            Quick Bills
                            <a href="#" class="text-red-600 text-xs hover:underline">View All</a>
                        </h3>
                        <div class="space-y-4">
                            <div class="flex items-center justify-between">
                                <div class="flex items-center gap-3">
                                    <div class="w-10 h-10 bg-blue-50 rounded-full flex items-center justify-center text-blue-600"><i class="fa-solid fa-bolt"></i></div>
                                    <div><p class="text-sm font-semibold">MSEB Electricity</p><p class="text-xs text-gray-400">Due in 2 days</p></div>
                                </div>
                                <button class="px-3 py-1 bg-red-50 text-red-600 rounded-full text-xs font-bold">Pay</button>
                            </div>
                            <div class="flex items-center justify-between">
                                <div class="flex items-center gap-3">
                                    <div class="w-10 h-10 bg-purple-50 rounded-full flex items-center justify-center text-purple-600"><i class="fa-solid fa-mobile-screen"></i></div>
                                    <div><p class="text-sm font-semibold">Airtel Postpaid</p><p class="text-xs text-gray-400">Bill Date 15th</p></div>
                                </div>
                                <button class="px-3 py-1 bg-red-50 text-red-600 rounded-full text-xs font-bold">Pay</button>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Main Grid -->
                <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
                    <div class="lg:col-span-2 space-y-6">
                        <!-- Your Accounts -->
                        <div class="bg-white rounded-2xl border p-6 card-shadow">
                            <h3 class="font-bold text-slate-800 mb-4">Your Accounts</h3>
                            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                                <div class="p-4 border rounded-xl hover:border-red-200 transition-colors cursor-pointer group">
                                    <div class="flex justify-between items-start mb-2">
                                        <span class="text-xs font-bold text-red-600 px-2 py-0.5 bg-red-50 rounded">Savings</span>
                                        <i class="fa-solid fa-chevron-right text-gray-300 group-hover:text-red-600 transition-all"></i>
                                    </div>
                                    <p class="text-gray-500 text-xs">Acc: XXXX ${user.accountDetails.accountNumber.substring(8)}</p>
                                    <p class="text-lg sm:text-xl font-bold text-slate-800 mt-1">₹ ${user.accountDetails.balance}</p>
                                </div>
                                <div class="p-4 border rounded-xl hover:border-blue-200 transition-colors cursor-pointer group">
                                    <div class="flex justify-between items-start mb-2">
                                        <span class="text-xs font-bold text-blue-600 px-2 py-0.5 bg-blue-50 rounded">Current</span>
                                        <i class="fa-solid fa-chevron-right text-gray-300 group-hover:text-blue-600 transition-all"></i>
                                    </div>
                                    <p class="text-gray-500 text-xs">Acc: XXXX 9901</p>
                                    <p class="text-lg sm:text-xl font-bold text-slate-800 mt-1">₹ 12,45,000.00</p>
                                </div>
                            </div>
                        </div>

                        <!-- Recent Transactions -->
                        <div class="bg-white rounded-2xl border p-6 card-shadow">
                            <h3 class="font-bold text-slate-800 mb-4 flex items-center justify-between">
                                Recent Transactions
                                <button class="text-red-600 text-sm font-medium">Download <i class="fa-solid fa-download ml-1"></i></button>
                            </h3>
                            <div class="space-y-4">
                                <div class="flex items-center justify-between p-3 rounded-lg hover:bg-gray-50 transition-colors">
                                    <div class="flex items-center gap-3 sm:gap-4">
                                        <div class="w-10 h-10 bg-gray-100 rounded-full flex items-center justify-center font-bold text-gray-500 flex-shrink-0">Z</div>
                                        <div class="min-w-0"><p class="text-sm font-semibold truncate">Zomato Order</p><p class="text-[10px] sm:text-xs text-gray-400">12 Oct 2025</p></div>
                                    </div>
                                    <div class="text-right flex-shrink-0">
                                        <p class="text-sm font-bold text-red-500">- ₹ 452.00</p>
                                        <p class="text-[8px] sm:text-[10px] text-gray-400 uppercase tracking-wider">UPI Ref: 45210...</p>
                                    </div>
                                </div>
                                <div class="flex items-center justify-between p-3 rounded-lg hover:bg-gray-50 transition-colors">
                                    <div class="flex items-center gap-3 sm:gap-4">
                                        <div class="w-10 h-10 bg-green-100 rounded-full flex items-center justify-center font-bold text-green-600 flex-shrink-0">S</div>
                                        <div class="min-w-0"><p class="text-sm font-semibold truncate">Salary Credit</p><p class="text-[10px] sm:text-xs text-gray-400">01 Oct 2025</p></div>
                                    </div>
                                    <div class="text-right flex-shrink-0">
                                        <p class="text-sm font-bold text-green-500">+ ₹ 1,25,000.00</p>
                                        <p class="text-[8px] sm:text-[10px] text-gray-400 uppercase tracking-wider">NFT Ref: SAL-2025</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Side Offers and Features -->
                    <div class="space-y-6">
                        <div class="bg-white rounded-2xl border p-6 card-shadow">
                            <h3 class="font-bold text-slate-800 mb-4">Need a Loan?</h3>
                            <div class="grid grid-cols-2 gap-3">
                                <div class="bg-gray-50 p-3 rounded-xl text-center hover:bg-red-50 transition-colors cursor-pointer group">
                                    <i class="fa-solid fa-user-graduate text-xl text-gray-400 group-hover:text-red-600 mb-2"></i>
                                    <p class="text-[10px] font-semibold">Education</p>
                                </div>
                                <div class="bg-gray-50 p-3 rounded-xl text-center hover:bg-red-50 transition-colors cursor-pointer group">
                                    <i class="fa-solid fa-car text-xl text-gray-400 group-hover:text-red-600 mb-2"></i>
                                    <p class="text-[10px] font-semibold">Car Loan</p>
                                </div>
                                <div class="bg-gray-50 p-3 rounded-xl text-center hover:bg-red-50 transition-colors cursor-pointer group">
                                    <i class="fa-solid fa-gem text-xl text-gray-400 group-hover:text-red-600 mb-2"></i>
                                    <p class="text-[10px] font-semibold">Gold Loan</p>
                                </div>
                                <div class="bg-red-600 p-3 rounded-xl text-center text-white cursor-pointer shadow-md">
                                    <i class="fa-solid fa-bolt text-xl mb-2"></i>
                                    <p class="text-[10px] font-semibold">Personal</p>
                                </div>
                            </div>
                            <button class="w-full mt-4 py-2 border-2 border-red-600 text-red-600 rounded-xl font-bold text-sm hover:bg-red-600 hover:text-white transition-all">Check Eligibility</button>
                        </div>

                        <div class="bg-white rounded-2xl border p-6 card-shadow">
                            <h3 class="font-bold text-slate-800 mb-4">Investments</h3>
                            <div class="space-y-4">
                                <div class="flex items-center justify-between p-3 border rounded-xl">
                                    <div class="flex items-center gap-3">
                                        <i class="fa-solid fa-vault text-blue-600"></i>
                                        <span class="text-sm font-medium">Fixed Deposit</span>
                                    </div>
                                    <button class="text-red-600 text-xs font-bold">Open</button>
                                </div>
                                <div class="flex items-center justify-between p-3 border rounded-xl">
                                    <div class="flex items-center gap-3">
                                        <i class="fa-solid fa-calendar-check text-green-600"></i>
                                        <span class="text-sm font-medium">Recurring</span>
                                    </div>
                                    <button class="text-red-600 text-xs font-bold">Open</button>
                                </div>
                            </div>
                        </div>

                        <div class="bg-slate-800 rounded-2xl p-6 text-white card-shadow">
                            <h3 class="font-bold mb-4">Manage Cards</h3>
                            <div class="relative h-32 w-full bg-gradient-to-r from-gray-700 to-gray-600 rounded-xl p-4 overflow-hidden mb-4">
                                <div class="absolute top-4 left-4 font-bold text-lg italic opacity-50">VISA</div>
                                <div class="absolute bottom-4 left-4 text-[10px] tracking-widest font-mono">XXXX XXXX XXXX 4242</div>
                                <div class="absolute bottom-4 right-4 font-bold text-xs">12/28</div>
                            </div>
                            <button class="w-full py-2 bg-white/10 hover:bg-white/20 transition-colors rounded-xl text-xs font-semibold">Apply for New Card</button>
                        </div>
                    </div>
                </div>
            </section>

            <!-- ACCOUNTS / DEPOSITS PAGE -->
            <section id="page-accounts" class="page-section hidden space-y-6">
                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div class="bg-white p-6 rounded-2xl border card-shadow">
                        <h3 class="text-lg font-bold mb-4 text-slate-800 border-b pb-2">Active Accounts</h3>
                        <div class="space-y-4">
                            <div class="p-4 bg-gray-50 rounded-xl">
                                <div class="flex justify-between items-center mb-1">
                                    <span class="font-bold">Savings Account</span>
                                    <span class="text-[10px] bg-green-100 text-green-700 px-2 py-1 rounded">Active</span>
                                </div>
                                <p class="text-[10px] text-gray-500 mb-2">Acc No: 1029384756</p>
                                <div class="flex justify-between items-end">
                                    <div>
                                        <p class="text-[10px] text-gray-400">Available Balance</p>
                                        <p class="text-lg font-bold">₹ 4,12,055.20</p>
                                    </div>
                                    <button class="text-red-600 text-xs font-bold">Statement</button>
                                </div>
                            </div>
                            <div class="p-4 bg-gray-50 rounded-xl opacity-80">
                                <div class="flex justify-between items-center mb-1">
                                    <span class="font-bold text-sm sm:text-base">Business Current Acc</span>
                                    <span class="text-[10px] bg-green-100 text-green-700 px-2 py-1 rounded">Active</span>
                                </div>
                                <p class="text-[10px] text-gray-500 mb-2">Acc No: 1223344556</p>
                                <div class="flex justify-between items-end">
                                    <div>
                                        <p class="text-[10px] text-gray-400">Available Balance</p>
                                        <p class="text-lg font-bold">₹ 12,45,000.00</p>
                                    </div>
                                    <button class="text-red-600 text-xs font-bold">Statement</button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="bg-white p-6 rounded-2xl border card-shadow">
                        <h3 class="text-lg font-bold mb-4 text-slate-800 border-b pb-2">Deposits</h3>
                        <div class="space-y-4">
                            <div class="p-4 bg-red-50 rounded-xl border border-red-100">
                                <div class="flex justify-between items-center mb-1">
                                    <span class="font-bold text-red-900 text-sm">Fixed Deposit #FD882</span>
                                    <span class="text-[10px] bg-red-100 text-red-700 px-2 py-1 rounded">Dec 2026</span>
                                </div>
                                <p class="text-[10px] text-red-700/60 mb-2">Interest Rate: 7.25% p.a.</p>
                                <div class="flex justify-between items-end">
                                    <div>
                                        <p class="text-[10px] text-red-700/40">Principal</p>
                                        <p class="text-base sm:text-lg font-bold text-red-900">₹ 5,00,000.00</p>
                                    </div>
                                    <div class="text-right">
                                        <p class="text-[10px] text-red-700/40">Expected</p>
                                        <p class="text-base sm:text-lg font-bold text-red-900">₹ 5,74,500.00</p>
                                    </div>
                                </div>
                            </div>
                            <div class="p-6 border-2 border-dashed rounded-xl flex flex-col items-center justify-center text-gray-400 hover:text-red-500 hover:border-red-200 cursor-pointer transition-all">
                                <i class="fa-solid fa-circle-plus text-2xl mb-2"></i>
                                <span class="font-bold text-sm">Open New Deposit</span>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <!-- FUND TRANSFER PAGE -->
            <section id="page-transfer" class="page-section hidden space-y-6">
                <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
                    <div class="lg:col-span-2 bg-white rounded-2xl border card-shadow p-6">
                        <div class="flex gap-4 border-b mb-6 overflow-x-auto no-scrollbar whitespace-nowrap">
                            <button class="tab-btn active px-4 py-2 text-xs sm:text-sm">Transfer Money</button>
                            <button class="tab-btn px-4 py-2 text-xs sm:text-sm text-gray-500">Beneficiaries</button>
                            <button class="tab-btn px-4 py-2 text-xs sm:text-sm text-gray-500">History</button>
                        </div>
                        
                        <div class="space-y-4 max-w-lg">
                            <div>
                                <label class="block text-[10px] font-bold text-gray-500 uppercase mb-2">From Account</label>
                                <select class="w-full p-3 border rounded-xl bg-gray-50 focus:ring-1 focus:ring-red-500 outline-none text-sm">
                                    <option>Savings - ...5678 (₹ 4.12L)</option>
                                    <option>Current - ...9901 (₹ 12.45L)</option>
                                </select>
                            </div>
                            <div>
                                <label class="block text-[10px] font-bold text-gray-500 uppercase mb-2">To Beneficiary</label>
                                <div class="flex gap-2">
                                    <select class="flex-1 p-3 border rounded-xl bg-gray-50 focus:ring-1 focus:ring-red-500 outline-none text-sm">
                                        <option>Rahul Kumar (HDFC)</option>
                                        <option>Megha Singh (SBI)</option>
                                        <option>Zomato Ltd (ICICI)</option>
                                    </select>
                                    <button class="bg-gray-100 px-3 rounded-xl text-gray-600 hover:bg-gray-200"><i class="fa-solid fa-plus"></i></button>
                                </div>
                            </div>
                            <div>
                                <label class="block text-[10px] font-bold text-gray-500 uppercase mb-2">Amount (₹)</label>
                                <input type="number" placeholder="Enter Amount" class="w-full p-3 border rounded-xl bg-gray-50 focus:ring-1 focus:ring-red-500 outline-none font-bold text-lg">
                            </div>
                            <div>
                                <label class="block text-[10px] font-bold text-gray-500 uppercase mb-2">Remarks</label>
                                <input type="text" placeholder="Optional" class="w-full p-3 border rounded-xl bg-gray-50 focus:ring-1 focus:ring-red-500 outline-none text-sm">
                            </div>
                            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-2">
                                <button class="py-3 border-2 border-red-600 text-red-600 rounded-xl font-bold text-sm">Schedule</button>
                                <button class="py-3 btn-primary rounded-xl font-bold text-sm">Pay Now</button>
                            </div>
                        </div>
                    </div>

                    <div class="space-y-6">
                        <div class="bg-white rounded-2xl border p-6 card-shadow">
                            <h3 class="font-bold text-slate-800 mb-4">Quick Pay</h3>
                            <div class="flex gap-4 overflow-x-auto no-scrollbar pb-2">
                                <div class="flex-shrink-0 text-center cursor-pointer">
                                    <div class="w-12 h-12 rounded-full bg-red-100 flex items-center justify-center text-red-600 mb-2 mx-auto"><i class="fa-solid fa-plus"></i></div>
                                    <p class="text-[10px] font-bold">Add New</p>
                                </div>
                                <div class="flex-shrink-0 text-center cursor-pointer">
                                    <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=Rahul" class="w-12 h-12 rounded-full mb-2 mx-auto">
                                    <p class="text-[10px] font-bold">Rahul</p>
                                </div>
                                <div class="flex-shrink-0 text-center cursor-pointer">
                                    <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=Megha" class="w-12 h-12 rounded-full mb-2 mx-auto">
                                    <p class="text-[10px] font-bold">Megha</p>
                                </div>
                            </div>
                        </div>
                        <div class="bg-blue-900 rounded-2xl p-6 text-white card-shadow relative overflow-hidden">
                            <h3 class="font-bold mb-2">Instant UPI</h3>
                            <p class="text-[10px] text-blue-200 mb-4">Send money instantly without adding beneficiary.</p>
                            <button class="w-full py-2 bg-white text-blue-900 rounded-lg text-xs font-bold">Pay via UPI</button>
                        </div>
                    </div>
                </div>
            </section>

            <!-- STATEMENT PAGE -->
            <section id="page-statement" class="page-section hidden">
                <div class="bg-white rounded-2xl border card-shadow overflow-hidden">
                    <div class="p-6 bg-gray-50 border-b flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
                        <div>
                            <h3 class="font-bold text-slate-800">Account Statements</h3>
                            <p class="text-[10px] sm:text-xs text-gray-500">History for any account</p>
                        </div>
                        <div class="flex gap-2 w-full sm:w-auto">
                            <button class="flex-1 sm:flex-none px-4 py-2 bg-white border rounded-lg text-xs hover:border-red-500 transition-all"><i class="fa-solid fa-filter mr-2"></i> Filters</button>
                            <button class="flex-1 sm:flex-none px-4 py-2 bg-red-600 text-white rounded-lg text-xs font-bold"><i class="fa-solid fa-file-pdf mr-2"></i> PDF</button>
                        </div>
                    </div>
                    <div class="p-4 sm:p-6 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 border-b">
                        <div>
                            <label class="text-[10px] font-bold text-gray-400 uppercase">Account</label>
                            <select class="w-full border-none bg-transparent font-medium focus:ring-0 text-sm">
                                <option>Savings Account ...5678</option>
                                <option>Current Account ...9901</option>
                            </select>
                        </div>
                        <div>
                            <label class="text-[10px] font-bold text-gray-400 uppercase">From</label>
                            <input type="date" class="w-full border-none bg-transparent font-medium focus:ring-0 text-sm" value="2025-09-01">
                        </div>
                        <div>
                            <label class="text-[10px] font-bold text-gray-400 uppercase">To</label>
                            <input type="date" class="w-full border-none bg-transparent font-medium focus:ring-0 text-sm" value="2025-10-12">
                        </div>
                        <div class="flex items-center">
                            <button class="w-full py-2 bg-slate-100 hover:bg-slate-200 rounded font-bold text-[10px]">REFRESH</button>
                        </div>
                    </div>
                    <div class="overflow-x-auto">
                        <table class="w-full text-left min-w-[600px]">
                            <thead class="bg-gray-50 border-b text-[10px] uppercase text-gray-400 font-bold">
                                <tr>
                                    <th class="px-6 py-3">Date</th>
                                    <th class="px-6 py-3">Description</th>
                                    <th class="px-6 py-3 text-right">Debit</th>
                                    <th class="px-6 py-3 text-right">Credit</th>
                                    <th class="px-6 py-3 text-right">Balance</th>
                                </tr>
                            </thead>
                            <tbody class="text-xs sm:text-sm">
                                <tr class="border-b hover:bg-gray-50">
                                    <td class="px-6 py-4 text-gray-500">12 Oct 2025</td>
                                    <td class="px-6 py-4 truncate max-w-[200px]">UPI/P2A/9872120032/Foodies</td>
                                    <td class="px-6 py-4 text-right text-red-500">- 120.00</td>
                                    <td class="px-6 py-4 text-right">--</td>
                                    <td class="px-6 py-4 text-right font-medium">4,12,055.20</td>
                                </tr>
                                <tr class="border-b hover:bg-gray-50">
                                    <td class="px-6 py-4 text-gray-500">10 Oct 2025</td>
                                    <td class="px-6 py-4 truncate max-w-[200px]">ATM CASH WDL/MUM/CENTRE</td>
                                    <td class="px-6 py-4 text-right text-red-500">- 5,000.00</td>
                                    <td class="px-6 py-4 text-right">--</td>
                                    <td class="px-6 py-4 text-right font-medium">4,12,175.20</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </section>

            <!-- LOANS PAGE -->
            <section id="page-loans" class="page-section hidden space-y-6">
                <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
                    <div class="lg:col-span-2 space-y-6">
                        <div class="bg-white p-6 rounded-2xl border card-shadow">
                            <h3 class="font-bold text-slate-800 mb-4">Active Loans</h3>
                            <div class="p-4 sm:p-5 border rounded-2xl relative overflow-hidden bg-white hover:border-red-200 transition-all">
                                <div class="absolute top-0 left-0 w-1 sm:w-2 h-full bg-red-600"></div>
                                <div class="flex justify-between items-start mb-4">
                                    <div>
                                        <h4 class="font-bold text-base sm:text-lg">Home Loan</h4>
                                        <p class="text-[10px] text-gray-500">L102938475</p>
                                    </div>
                                    <span class="text-[10px] bg-green-100 text-green-700 px-2 py-1 rounded-full font-bold">82% Paid</span>
                                </div>
                                <div class="grid grid-cols-3 gap-2 sm:gap-4 mb-6 text-center sm:text-left">
                                    <div><p class="text-[8px] sm:text-[10px] text-gray-400 uppercase">Sanctioned</p><p class="font-bold text-xs sm:text-base">₹ 45L</p></div>
                                    <div><p class="text-[8px] sm:text-[10px] text-gray-400 uppercase">ROI</p><p class="font-bold text-xs sm:text-base">8.75%</p></div>
                                    <div><p class="text-[8px] sm:text-[10px] text-gray-400 uppercase">O/S</p><p class="font-bold text-red-600 text-xs sm:text-base">₹ 7.5L</p></div>
                                </div>
                                <div class="w-full bg-gray-100 h-1.5 sm:h-2 rounded-full mb-6">
                                    <div class="bg-green-500 h-full rounded-full" style="width: 82%"></div>
                                </div>
                                <div class="flex flex-col sm:flex-row gap-3">
                                    <button class="flex-1 py-2 border rounded-lg text-xs font-bold hover:bg-gray-50">Details</button>
                                    <button class="flex-1 py-2 bg-red-600 text-white rounded-lg text-xs font-bold">Pay EMI</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <!-- CARDS PAGE -->
            <section id="page-cards" class="page-section hidden space-y-6">
                <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                    <div class="bg-white p-6 rounded-2xl border card-shadow space-y-6">
                        <div class="h-44 sm:h-48 rounded-2xl bg-gradient-to-br from-indigo-600 to-purple-800 p-6 text-white relative shadow-xl overflow-hidden">
                            <div class="flex justify-between items-start relative z-10">
                                <span class="text-[10px] sm:text-sm font-bold italic">NEXGEN PREMIUM</span>
                                <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/5/5e/Visa_Inc._logo.svg/2560px-Visa_Inc._logo.svg.png" class="h-3 sm:h-4 invert" alt="Visa">
                            </div>
                            <div class="mt-10 sm:mt-12 text-base sm:text-xl tracking-widest font-mono relative z-10">4242 9801 1234 5678</div>
                            <div class="mt-4 flex justify-between items-end relative z-10">
                                <div><p class="text-[8px] uppercase opacity-60">Holder</p><p class="text-[10px] sm:text-sm font-bold">RAJESH KHANNA</p></div>
                                <div><p class="text-[8px] uppercase opacity-60">Exp</p><p class="text-[10px] sm:text-sm font-bold">12 / 28</p></div>
                            </div>
                        </div>
                        <div class="space-y-3">
                            <div class="flex items-center justify-between p-3 bg-gray-50 rounded-xl">
                                <span class="text-xs font-medium">Card Status</span>
                                <span class="text-[10px] font-bold text-green-600">Active</span>
                            </div>
                            <button class="w-full py-2 border-2 border-red-600 text-red-600 rounded-xl font-bold text-xs">Reset PIN</button>
                        </div>
                    </div>
                </div>
            </section>

            <!-- SETTINGS PAGE -->
            <section id="page-settings" class="page-section hidden max-w-4xl mx-auto">
                <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
                    <nav class="space-y-2">
                        <button class="w-full text-left p-4 rounded-xl bg-white border border-red-100 text-red-600 font-bold shadow-sm flex items-center justify-between text-sm">
                            Account <i class="fa-solid fa-chevron-right text-xs"></i>
                        </button>
                        <button class="w-full text-left p-4 rounded-xl hover:bg-white text-gray-600 font-medium transition-all flex items-center justify-between text-sm">
                            Cards <i class="fa-solid fa-chevron-right text-xs opacity-50"></i>
                        </button>
                    </nav>
                    <div class="md:col-span-2 bg-white p-6 sm:p-8 rounded-2xl border card-shadow">
                        <h3 class="text-lg sm:text-xl font-bold mb-6">Account Settings</h3>
                        <div class="space-y-6">
                            <div class="flex items-center justify-between py-4 border-b">
                                <div><p class="font-bold text-sm">E-Statements</p><p class="text-[10px] text-gray-500">Receive via email</p></div>
                                <div class="w-8 h-4 bg-red-600 rounded-full flex items-center px-1"><div class="w-2.5 h-2.5 bg-white rounded-full ml-auto"></div></div>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

            <!-- PROFILE PAGE -->
            <section id="page-profile" class="page-section hidden max-w-4xl mx-auto space-y-6">
                <div class="bg-white rounded-2xl border p-6 sm:p-8 card-shadow flex flex-col items-center sm:items-start sm:flex-row gap-6 text-center sm:text-left">
                    <div class="w-24 h-24 sm:w-32 sm:h-32 rounded-full border-4 border-red-50 overflow-hidden flex-shrink-0">
                        <img src="https://api.dicebear.com/7.x/avataaars/svg?seed=Rajesh" class="w-full h-full object-cover">
                    </div>
                    <div class="flex-1 min-w-0">
                        <h2 class="text-xl sm:text-2xl font-bold truncate">Rajesh Khanna</h2>
                        <p class="text-gray-500 text-sm">ID: 991208342</p>
                        <div class="mt-4 flex flex-wrap gap-2 justify-center sm:justify-start">
                            <span class="px-2 py-1 bg-red-50 text-red-600 text-[10px] font-bold rounded-full">Premium</span>
                            <span class="px-2 py-1 bg-blue-50 text-blue-600 text-[10px] font-bold rounded-full">KYC Verified</span>
                        </div>
                    </div>
                    <button class="px-4 py-2 btn-primary rounded-xl font-bold text-xs sm:text-sm">Update Password</button>
                </div>

                <div class="grid grid-cols-1 sm:grid-cols-2 gap-6">
                    <div class="bg-white p-6 rounded-2xl border card-shadow">
                        <h3 class="font-bold text-slate-800 mb-4 text-sm">Personal Info</h3>
                        <div class="space-y-3">
                            <div><p class="text-[8px] text-gray-400 uppercase font-bold">Email</p><p class="text-xs font-semibold truncate">rajesh.k@nexgenbank.com</p></div>
                            <div><p class="text-[8px] text-gray-400 uppercase font-bold">Mobile</p><p class="text-xs font-semibold">+91 98765 43210</p></div>
                        </div>
                    </div>
                    <div class="bg-white p-6 rounded-2xl border card-shadow">
                        <h3 class="font-bold text-slate-800 mb-4 text-sm">Documents</h3>
                        <div class="space-y-2">
                            <div class="flex items-center justify-between p-2 bg-gray-50 rounded-lg">
                                <span class="text-[10px] font-bold">PAN Card</span>
                                <i class="fa-solid fa-circle-check text-green-500 text-xs"></i>
                            </div>
                            <div class="flex items-center justify-between p-2 bg-gray-50 rounded-lg">
                                <span class="text-[10px] font-bold">Aadhaar</span>
                                <i class="fa-solid fa-circle-check text-green-500 text-xs"></i>
                            </div>
                        </div>
                    </div>
                </div>
            </section>

        </div>
    </main>

    <script>
        function toggleMobileMenu() {
            const sidebar = document.getElementById('sidebar');
            const backdrop = document.getElementById('sidebar-backdrop');
            
            if (sidebar.classList.contains('hidden-mobile')) {
                sidebar.classList.remove('hidden-mobile');
                sidebar.classList.add('show-mobile');
                backdrop.classList.remove('hidden');
            } else {
                sidebar.classList.add('hidden-mobile');
                sidebar.classList.remove('show-mobile');
                backdrop.classList.add('hidden');
            }
        }

        function showPage(pageId) {
            // Hide all pages
            document.querySelectorAll('.page-section').forEach(section => {
                section.classList.add('hidden');
            });
            
            // Show requested page
            document.getElementById('page-' + pageId).classList.remove('hidden');
            
            // Update sidebar active state
            document.querySelectorAll('.sidebar-link').forEach(link => {
                link.classList.remove('active');
            });
            document.getElementById('nav-' + pageId).classList.add('active');

            // Close mobile menu if open
            const sidebar = document.getElementById('sidebar');
            if (sidebar.classList.contains('show-mobile')) {
                toggleMobileMenu();
            }

            // Update Header Title
            const titles = {
                'home': 'Welcome, Rajesh!',
                'accounts': 'Accounts & Deposits',
                'transfer': 'Fund Transfer',
                'statement': 'Statements',
                'loans': 'Loans',
                'cards': 'Cards',
                'settings': 'Settings',
                'profile': 'My Profile'
            };
            document.getElementById('page-title').innerText = titles[pageId];

            // Scroll container to top
            document.getElementById('content-container').scrollTop = 0;
        }

        // Add small functionality for UI interaction
        document.querySelectorAll('.tab-btn').forEach(btn => {
            btn.addEventListener('click', function() {
                const parent = this.parentElement;
                parent.querySelectorAll('.tab-btn').forEach(b => b.classList.remove('active'));
                this.classList.add('active');
            });
        });
    </script>
</body>
</html>