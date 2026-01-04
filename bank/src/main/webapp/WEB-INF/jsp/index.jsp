<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>NexGenBank | The Future of Digital Wealth</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <script src="https://unpkg.com/lucide@latest"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/gsap/3.12.2/gsap.min.js"></script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&display=swap');
        
        :root {
            --brand-red: #e11d48;
            --brand-dark: #0f172a;
        }

        body { 
            font-family: 'Plus Jakarta Sans', sans-serif; 
            scroll-behavior: smooth; 
            -webkit-tap-highlight-color: transparent; 
        }

        .glass-nav { 
            background: rgba(255, 255, 255, 0.7); 
            backdrop-filter: blur(20px); 
            -webkit-backdrop-filter: blur(20px);
            border-bottom: 1px solid rgba(255, 255, 255, 0.3);
        }

        .hero-gradient {
            background: radial-gradient(circle at 50% 50%, rgba(225, 29, 72, 0.05) 0%, rgba(255, 255, 255, 0) 70%);
        }

        .step-content {
            display: none;
            opacity: 0;
            transform: translateY(10px);
        }

        .step-content.active {
            display: block;
        }

        .view-hidden { display: none !important; }

        .gradient-text {
            background: linear-gradient(135deg, #0f172a 30%, #e11d48 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }

        .btn-primary {
            background: var(--brand-dark);
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        }

        .btn-primary:hover {
            background: var(--brand-red);
            transform: translateY(-2px);
            box-shadow: 0 10px 20px -5px rgba(225, 29, 72, 0.3);
        }

        .error-shake {
            animation: shake 0.4s focus-in-expand;
            border-color: #ef4444 !important;
        }

        @keyframes shake {
            0%, 100% { transform: translateX(0); }
            25% { transform: translateX(-5px); }
            75% { transform: translateX(5px); }
        }

        .input-focus-effect:focus {
            box-shadow: 0 0 0 4px rgba(225, 29, 72, 0.1);
            border-color: var(--brand-red);
        }

        /* Custom Scrollbar */
        ::-webkit-scrollbar { width: 6px; }
        ::-webkit-scrollbar-track { background: transparent; }
        ::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }
    </style>
</head>
<body class="bg-white text-slate-900 overflow-x-hidden antialiased">

    <!-- Navbar -->
    <nav class="fixed top-0 left-0 right-0 z-[100] glass-nav h-16 md:h-20 flex items-center">
        <div class="max-w-7xl mx-auto px-6 w-full flex items-center justify-between">
            <div class="flex items-center space-x-2 cursor-pointer group" onclick="location.reload()">
                <div class="w-10 h-10 bg-slate-900 rounded-xl flex items-center justify-center shadow-lg transition-transform group-hover:rotate-12">
                    <i data-lucide="component" class="text-white w-6 h-6"></i>
                </div>
                <span class="text-xl font-extrabold tracking-tighter">NexGen<span class="text-rose-600">Bank</span></span>
            </div>
            
            <div class="hidden md:flex items-center space-x-8 text-sm font-semibold text-slate-600">
                <a href="#features" class="hover:text-rose-600 transition-colors">Features</a>
                <a href="#" class="hover:text-rose-600 transition-colors">Security</a>
                <a href="#" class="hover:text-rose-600 transition-colors">Pricing</a>
            </div>

            <div class="flex items-center space-x-4">
                <button id="nav-back-btn" onclick="prevStep()" class="view-hidden flex items-center space-x-1 text-slate-500 hover:text-slate-900 transition-colors">
                    <i data-lucide="chevron-left" class="w-5 h-5"></i>
                    <span class="text-xs font-bold uppercase tracking-wider">Back</span>
                </button>
                <button onclick="startOnboarding()" class="btn-primary text-white px-6 py-2.5 rounded-full text-xs font-bold">
                    Open Account
                </button>
            </div>
        </div>
    </nav>

    <!-- Landing Page -->
    <main id="landing-view" class="hero-gradient">
        <!-- Hero -->
        <section class="pt-40 pb-20 px-6 text-center">
            <div class="max-w-4xl mx-auto">
                <div class="inline-flex items-center space-x-2 bg-slate-50 border border-slate-100 px-4 py-2 rounded-full mb-8">
                    <span class="w-2 h-2 bg-rose-500 rounded-full animate-pulse"></span>
                    <span class="text-[10px] font-bold uppercase tracking-widest text-slate-500">New: Multi-currency Vaults released</span>
                </div>
                <h1 class="text-5xl md:text-8xl font-extrabold leading-[1.1] mb-8 tracking-tight">
                    Banking for the <br/><span class="gradient-text italic">Next Generation.</span>
                </h1>
                <p class="text-lg md:text-xl text-slate-500 max-w-2xl mx-auto mb-12 font-medium leading-relaxed">
                    Stop settling for legacy banking. Experience hyper-digital finance with 5.0% APY, global transfers, and AI-driven wealth management.
                </p>
                <div class="flex flex-col sm:flex-row items-center justify-center space-y-4 sm:space-y-0 sm:space-x-6">
                    <button onclick="startOnboarding()" class="w-full sm:w-auto bg-rose-600 text-white px-10 py-5 rounded-2xl font-bold text-lg shadow-2xl shadow-rose-500/20 hover:bg-rose-700 transition-all flex items-center justify-center">
                        Start Your Journey <i data-lucide="arrow-right" class="ml-2 w-5 h-5"></i>
                    </button>
                    <button class="w-full sm:w-auto bg-white border border-slate-200 text-slate-900 px-10 py-5 rounded-2xl font-bold text-lg hover:bg-slate-50 transition-all">
                        Compare Plans
                    </button>
                </div>
            </div>
        </section>

        <!-- Features -->
        <section id="features" class="max-w-7xl mx-auto px-6 py-24 border-t border-slate-50">
            <div class="grid md:grid-cols-3 gap-12">
                <div class="space-y-4">
                    <div class="w-12 h-12 bg-rose-50 rounded-2xl flex items-center justify-center text-rose-600">
                        <i data-lucide="zap"></i>
                    </div>
                    <h3 class="text-xl font-bold">Instant Setup</h3>
                    <p class="text-slate-500 text-sm leading-relaxed">Open a fully-functional account in under 3 minutes with zero paperwork and instant KYC.</p>
                </div>
                <div class="space-y-4">
                    <div class="w-12 h-12 bg-indigo-50 rounded-2xl flex items-center justify-center text-indigo-600">
                        <i data-lucide="shield-check"></i>
                    </div>
                    <h3 class="text-xl font-bold">Military-Grade Security</h3>
                    <p class="text-slate-500 text-sm leading-relaxed">Your assets are protected by 256-bit encryption and multi-sig authorization protocols.</p>
                </div>
                <div class="space-y-4">
                    <div class="w-12 h-12 bg-emerald-50 rounded-2xl flex items-center justify-center text-emerald-600">
                        <i data-lucide="globe"></i>
                    </div>
                    <h3 class="text-xl font-bold">Global Presence</h3>
                    <p class="text-slate-500 text-sm leading-relaxed">Spend like a local in 150+ countries with real-time exchange rates and zero hidden fees.</p>
                </div>
            </div>
        </section>
    </main>

    <!-- Onboarding Section -->
    <section id="onboarding-view" class="view-hidden min-h-screen pt-32 pb-20 px-6 bg-slate-50/50">
        <div class="max-w-2xl mx-auto">
            
            <!-- Progress Stepper -->
            <div id="stepper-ui" class="flex items-center justify-between mb-16 relative px-4">
                <div class="absolute top-5 left-0 right-0 h-0.5 bg-slate-200 -z-10 mx-10"></div>
                <div id="progress-line" class="absolute top-5 left-0 h-0.5 bg-rose-600 -z-10 transition-all duration-700 mx-10" style="width: 0%"></div>
                
                <!-- Step 1 Bubble -->
                <div class="flex flex-col items-center">
                    <div class="step-bubble w-10 h-10 rounded-full bg-rose-600 text-white flex items-center justify-center text-xs font-bold border-4 border-white shadow-lg ring-4 ring-rose-50 transition-all">1</div>
                    <span class="text-[10px] font-bold uppercase text-rose-600 mt-3 tracking-wider">Plan</span>
                </div>
                <!-- Step 2 Bubble -->
                <div class="flex flex-col items-center">
                    <div class="step-bubble w-10 h-10 rounded-full bg-white text-slate-300 flex items-center justify-center text-xs font-bold border-4 border-white shadow-sm ring-4 ring-slate-100 transition-all">2</div>
                    <span class="text-[10px] font-bold uppercase text-slate-400 mt-3 tracking-wider">Profile</span>
                </div>
                <!-- Step 3 Bubble -->
                <div class="flex flex-col items-center">
                    <div class="step-bubble w-10 h-10 rounded-full bg-white text-slate-300 flex items-center justify-center text-xs font-bold border-4 border-white shadow-sm ring-4 ring-slate-100 transition-all">3</div>
                    <span class="text-[10px] font-bold uppercase text-slate-400 mt-3 tracking-wider">Identity</span>
                </div>
            </div>

            <!-- Form Container -->
            <div class="bg-white rounded-[3rem] p-8 md:p-14 border border-slate-100 shadow-xl relative overflow-hidden">
                
                <!-- Step 1: Account Selection -->
                <div id="step-1" class="step-content active">
                    <div class="text-center mb-12">
                        <h2 class="text-3xl font-extrabold mb-3">Choose Your Experience</h2>
                        <p class="text-slate-500">Select a plan that fits your financial lifestyle.</p>
                    </div>
                    <div class="grid gap-6">
                        <button onclick="selectAccount('SAVINGS')" class="group p-8 rounded-[2.5rem] border-2 border-slate-50 text-left hover:border-rose-600 hover:bg-rose-50/30 transition-all flex items-center space-x-6">
                            <div class="w-16 h-16 bg-rose-50 rounded-2xl flex items-center justify-center text-rose-600 group-hover:scale-110 transition-transform">
                                <i data-lucide="piggy-bank" class="w-8 h-8"></i>
                            </div>
                            <div class="flex-1">
                                <h3 class="font-bold text-xl mb-1">Savings Plus</h3>
                                <p class="text-sm text-slate-500 italic">5.0% APY • No Minimum • Free ATM Withdrawals</p>
                            </div>
                            <i data-lucide="chevron-right" class="text-slate-300 group-hover:text-rose-600 transition-colors"></i>
                        </button>

                        <button onclick="selectAccount('CURRENT')" class="group p-8 rounded-[2.5rem] border-2 border-slate-50 text-left hover:border-slate-900 hover:bg-slate-50 transition-all flex items-center space-x-6">
                            <div class="w-16 h-16 bg-slate-100 rounded-2xl flex items-center justify-center text-slate-900 group-hover:scale-110 transition-transform">
                                <i data-lucide="briefcase" class="w-8 h-8"></i>
                            </div>
                            <div class="flex-1">
                                <h3 class="font-bold text-xl mb-1">NexGen Current</h3>
                                <p class="text-sm text-slate-500 italic">Global Transfers • Business Perks • Multi-currency</p>
                            </div>
                            <i data-lucide="chevron-right" class="text-slate-300 group-hover:text-slate-900 transition-colors"></i>
                        </button>
                    </div>
                </div>

                <!-- Step 2: Personal Info -->
                <div id="step-2" class="step-content">
                    <div class="text-center mb-10">
                        <h2 class="text-3xl font-extrabold mb-3">Your Profile</h2>
                        <p class="text-slate-500">Tell us a bit about yourself.</p>
                    </div>
                    <div class="space-y-6">
                        <div class="relative group">
                            <input id="form-name" type="text" placeholder="Full Legal Name" class="w-full px-6 py-4 rounded-2xl border border-slate-200 outline-none transition-all input-focus-effect font-medium">
                            <i data-lucide="user" class="absolute right-6 top-1/2 -translate-y-1/2 text-slate-300 group-focus-within:text-rose-600 transition-colors w-5 h-5"></i>
                        </div>
                        
                        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <div class="space-y-2">
                                <input id="form-phone" type="tel" placeholder="Mobile Number" class="w-full px-6 py-4 rounded-2xl border border-slate-200 outline-none transition-all input-focus-effect font-medium">
                                <p id="err-phone" class="view-hidden text-[10px] text-rose-600 font-bold uppercase ml-2 tracking-wider">Number already in use.</p>
                            </div>
                            <div class="space-y-2">
                                <input id="form-email" type="email" placeholder="Email Address" class="w-full px-6 py-4 rounded-2xl border border-slate-200 outline-none transition-all input-focus-effect font-medium">
                                <p id="err-email" class="view-hidden text-[10px] text-rose-600 font-bold uppercase ml-2 tracking-wider">Email registered.</p>
                            </div>
                        </div>

                        <div class="space-y-2">
                            <div class="relative group">
                                <input id="form-password" type="password" placeholder="Create Secure Password" class="w-full px-6 py-4 rounded-2xl border border-slate-200 outline-none transition-all input-focus-effect font-medium">
                                <i data-lucide="lock" class="absolute right-6 top-1/2 -translate-y-1/2 text-slate-300 group-focus-within:text-rose-600 transition-colors w-5 h-5"></i>
                            </div>
                            <p id="err-pass" class="view-hidden text-[10px] text-rose-600 font-bold uppercase ml-2 tracking-wider">Min 8 characters required.</p>
                        </div>

                        <button onclick="nextStep()" class="w-full bg-rose-600 text-white py-5 rounded-2xl font-bold text-lg shadow-xl hover:bg-rose-700 transition-all active:scale-95">
                            Continue
                        </button>
                    </div>
                </div>

                <!-- Step 3: Identity Verification -->
                <div id="step-3" class="step-content">
                    <div class="text-center mb-10">
                        <div class="w-16 h-16 bg-blue-50 text-blue-600 rounded-full flex items-center justify-center mx-auto mb-6">
                            <i data-lucide="fingerprint" class="w-8 h-8"></i>
                        </div>
                        <h2 class="text-3xl font-extrabold mb-3">Identity Check</h2>
                        <p class="text-slate-500">Securely verify your identity.</p>
                    </div>
                    <div class="space-y-8">
                        <div class="space-y-2">
                            <label class="text-[10px] font-extrabold uppercase text-slate-400 ml-4 tracking-widest">National ID / PAN</label>
                            <input id="pan-input" type="text" maxLength="10" placeholder="ABCDE1234F" class="w-full px-6 py-5 rounded-2xl border border-slate-200 outline-none transition-all input-focus-effect font-mono uppercase text-lg text-center tracking-widest">
                            <p id="err-pan" class="view-hidden text-[10px] text-rose-600 font-bold uppercase ml-2 tracking-wider">Invalid or used PAN.</p>
                        </div>
                        
                        <div class="space-y-2">
                            <label class="text-[10px] font-extrabold uppercase text-slate-400 ml-4 tracking-widest">Aadhaar Number</label>
                            <input id="aadhar-input" type="tel" maxLength="14" placeholder="0000 0000 0000" class="w-full px-6 py-5 rounded-2xl border border-slate-200 outline-none transition-all input-focus-effect font-mono text-lg text-center tracking-widest">
                            <p id="err-aadhar" class="view-hidden text-[10px] text-rose-600 font-bold uppercase ml-2 tracking-wider">Invalid or used Aadhaar.</p>
                        </div>

                        <div class="p-4 bg-slate-50 rounded-2xl flex items-start space-x-3">
                            <i data-lucide="shield-check" class="text-slate-400 w-5 h-5 mt-0.5"></i>
                            <p class="text-[11px] text-slate-500 leading-relaxed font-medium">By clicking finalize, you agree to NexGenBank's Terms of Service and authorize identity verification via regulatory APIs.</p>
                        </div>

                        <button id="final-btn" onclick="nextStep()" class="w-full bg-slate-900 text-white py-5 rounded-2xl font-bold text-lg shadow-xl hover:bg-rose-600 transition-all active:scale-95">
                            Finalize Application
                        </button>
                    </div>
                </div>

                <!-- Step 4: Success -->
                <div id="step-4" class="step-content text-center">
                    <div id="success-animation" class="w-32 h-32 bg-emerald-500 rounded-full flex items-center justify-center mx-auto mb-10 border-[10px] border-emerald-50">
                        <i data-lucide="check" class="text-white w-16 h-16"></i>
                    </div>
                    <h2 class="text-4xl font-extrabold mb-4 tracking-tight">You're In!</h2>
                    <p class="text-slate-500 mb-10 text-lg">Your account for <span id="summary-name" class="text-slate-900 font-bold"></span> is active and ready for deposits.</p>
                    
                    <div class="space-y-4">
                        <button onclick="location.reload()" class="w-full bg-slate-900 text-white py-5 rounded-2xl font-bold text-lg hover:bg-rose-600 transition-all">
                            Enter Member Dashboard
                        </button>
                        <p class="text-xs text-slate-400 font-bold uppercase tracking-widest">A welcome kit has been sent to your email.</p>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <script>
        let currentStep = 1;
        let regData = { fullName: "", email: "", phone: "", password: "", pan: "", aadhar: "", accountType: "" };

        lucide.createIcons();

        function startOnboarding() {
            gsap.to(window, { duration: 0.5, scrollTo: 0 });
            gsap.to('#landing-view', { opacity: 0, y: -20, duration: 0.4, onComplete: () => {
                document.getElementById('landing-view').classList.add('view-hidden');
                document.getElementById('onboarding-view').classList.remove('view-hidden');
                gsap.fromTo('#onboarding-view', { opacity: 0, y: 30 }, { opacity: 1, y: 0, duration: 0.6 });
                showStep(1);
            }});
        }

        function showStep(s) {
            // Animation logic
            const prevActive = document.querySelector('.step-content.active');
            if (prevActive) prevActive.classList.remove('active');
            
            const nextEl = document.getElementById('step-' + s);
            nextEl.classList.add('active');
            gsap.fromTo(nextEl, { opacity: 0, y: 15 }, { opacity: 1, y: 0, duration: 0.5, ease: "power2.out" });

            // Navigation Visibility
            const backBtn = document.getElementById('nav-back-btn');
            (s > 1 && s < 4) ? backBtn.classList.remove('view-hidden') : backBtn.classList.add('view-hidden');

            updateStepper(s);
        }

        function updateStepper(s) {
            if (s > 3) {
                document.getElementById('stepper-ui').classList.add('view-hidden');
                return;
            }

            const bubbles = document.querySelectorAll('.step-bubble');
            const labels = document.querySelectorAll('#stepper-ui span');
            const progress = document.getElementById('progress-line');
            
            progress.style.width = ((s - 1) / 2) * 100 + '%';

            bubbles.forEach((b, i) => {
                const stepNum = i + 1;
                if (stepNum < s) {
                    // Completed
                    b.innerHTML = '<i data-lucide="check" class="w-5 h-5"></i>';
                    b.className = "step-bubble w-10 h-10 rounded-full bg-rose-600 text-white flex items-center justify-center text-xs font-bold border-4 border-white shadow-lg ring-4 ring-rose-50 transition-all scale-90";
                    labels[i].className = "text-[10px] font-bold uppercase text-rose-400 mt-3 tracking-wider";
                } else if (stepNum === s) {
                    // Active
                    b.innerHTML = stepNum;
                    b.className = "step-bubble w-10 h-10 rounded-full bg-rose-600 text-white flex items-center justify-center text-xs font-bold border-4 border-white shadow-lg ring-4 ring-rose-50 transition-all scale-110";
                    labels[i].className = "text-[10px] font-bold uppercase text-rose-600 mt-3 tracking-wider";
                } else {
                    // Pending
                    b.innerHTML = stepNum;
                    b.className = "step-bubble w-10 h-10 rounded-full bg-white text-slate-300 flex items-center justify-center text-xs font-bold border-4 border-white shadow-sm ring-4 ring-slate-100 transition-all";
                    labels[i].className = "text-[10px] font-bold uppercase text-slate-400 mt-3 tracking-wider";
                }
            });
            lucide.createIcons();
        }

        function selectAccount(type) {
            regData.accountType = type;
            currentStep = 2;
            showStep(2);
        }

        function prevStep() {
            if (currentStep > 1) {
                currentStep--;
                showStep(currentStep);
            }
        }

        function clearErrors() {
            ['err-email', 'err-phone', 'err-pan', 'err-aadhar', 'err-pass'].forEach(id => {
                document.getElementById(id).classList.add('view-hidden');
            });
            document.querySelectorAll('input').forEach(i => i.classList.remove('error-shake', 'border-rose-600'));
        }

        async function nextStep() {
            clearErrors();
            
            if (currentStep === 2) {
                regData.fullName = document.getElementById('form-name').value;
                regData.email = document.getElementById('form-email').value;
                regData.phone = document.getElementById('form-phone').value;
                regData.password = document.getElementById('form-password').value;

                if (!regData.fullName || regData.password.length < 8) {
                    if (regData.password.length < 8) {
                        const passInput = document.getElementById('form-password');
                        passInput.classList.add('error-shake');
                        document.getElementById('err-pass').classList.remove('view-hidden');
                    }
                    return;
                }
                currentStep = 3; showStep(3);
            } else if (currentStep === 3) {
                regData.pan = document.getElementById('pan-input').value.toUpperCase();
                regData.aadhar = document.getElementById('aadhar-input').value.replace(/\s/g, '');
                
                if (regData.pan.length < 10) {
                    document.getElementById('pan-input').classList.add('error-shake');
                    return;
                }
                if (regData.aadhar.length < 12) {
                    document.getElementById('aadhar-input').classList.add('error-shake');
                    return;
                }
                
                await submitToBackend();
            }
        }

        async function submitToBackend() {
            const btn = document.getElementById('final-btn');
            btn.disabled = true;
            btn.innerHTML = `<span class="flex items-center justify-center"><i data-lucide="loader-2" class="animate-spin mr-2"></i> Verifying...</span>`;
            lucide.createIcons();

            try {
                // Keeping original logic flow as requested
                const res = await fetch('/api/onboarding/register?type=' + regData.accountType, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(regData)
                });

                const resultText = await res.text();

                if (resultText === "SUCCESS") {
                    document.getElementById('summary-name').innerText = regData.fullName;
                    currentStep = 4;
                    showStep(4);
                    gsap.from('#success-animation', { scale: 0, rotate: -45, duration: 0.8, ease: "back.out(1.7)" });
                } else {
                    btn.disabled = false;
                    btn.innerText = 'Finalize Application';
                    
                    if (resultText.includes("EXISTS_EMAIL")) {
                        currentStep = 2; showStep(2);
                        document.getElementById('err-email').classList.remove('view-hidden');
                        document.getElementById('form-email').classList.add('error-shake');
                    } else if (resultText.includes("EXISTS_PHONE")) {
                        currentStep = 2; showStep(2);
                        document.getElementById('err-phone').classList.remove('view-hidden');
                        document.getElementById('form-phone').classList.add('error-shake');
                    } else if (resultText.includes("EXISTS_PAN")) {
                        document.getElementById('err-pan').classList.remove('view-hidden');
                        document.getElementById('pan-input').classList.add('error-shake');
                    } else if (resultText.includes("EXISTS_AADHAR")) {
                        document.getElementById('err-aadhar').classList.remove('view-hidden');
                        document.getElementById('aadhar-input').classList.add('error-shake');
                    }
                }
            } catch(e) { 
                btn.disabled = false;
                btn.innerText = 'Finalize Application';
            }
        }

        // Live Formatting for Aadhaar
        document.getElementById('aadhar-input').addEventListener('input', function (e) {
            let val = e.target.value.replace(/\D/g, '');
            let formatted = val.match(/.{1,4}/g)?.join(' ') || val;
            e.target.value = formatted.substring(0, 14);
        });

        // Input reset on type
        document.querySelectorAll('input').forEach(input => {
            input.addEventListener('input', () => {
                input.classList.remove('error-shake');
                const errId = input.id.replace('form-', 'err-').replace('-input', '');
                const errEl = document.getElementById('err-' + errId) || document.getElementById('err-' + input.id.split('-')[1]);
                if(errEl) errEl.classList.add('view-hidden');
            });
        });
    </script>
</body>
</html>