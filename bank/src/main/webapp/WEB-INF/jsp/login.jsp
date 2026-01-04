<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>NexGenBank | Secure Login</title>
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

        .login-gradient {
            background: radial-gradient(circle at 0% 0%, rgba(225, 29, 72, 0.03) 0%, rgba(255, 255, 255, 0) 50%),
                        radial-gradient(circle at 100% 100%, rgba(15, 23, 42, 0.03) 0%, rgba(255, 255, 255, 0) 50%);
        }

        .glass-card {
            background: rgba(255, 255, 255, 0.8);
            backdrop-filter: blur(20px);
            border: 1px solid rgba(255, 255, 255, 0.4);
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.08);
        }

        .input-focus-effect:focus {
            box-shadow: 0 0 0 4px rgba(225, 29, 72, 0.1);
            border-color: var(--brand-red);
        }

        .error-shake {
            animation: shake 0.4s cubic-bezier(.36,.07,.19,.97) both;
            border-color: #ef4444 !important;
        }

        @keyframes shake {
            10%, 90% { transform: translate3d(-1px, 0, 0); }
            20%, 80% { transform: translate3d(2px, 0, 0); }
            30%, 50%, 70% { transform: translate3d(-4px, 0, 0); }
            40%, 60% { transform: translate3d(4px, 0, 0); }
        }

        .view-hidden { display: none !important; }
    </style>
</head>
<body class="bg-[#fcfdfe] text-slate-900 min-h-screen flex flex-col login-gradient">

    <!-- Simple Nav -->
    <nav class="p-6 flex justify-between items-center max-w-7xl mx-auto w-full">
        <div class="flex items-center space-x-2 cursor-pointer" onclick="window.location.href='/'">
            <div class="w-8 h-8 bg-slate-900 rounded-lg flex items-center justify-center">
                <i data-lucide="component" class="text-white w-5 h-5"></i>
            </div>
            <span class="text-lg font-extrabold tracking-tighter">NexGen<span class="text-rose-600">Bank</span></span>
        </div>
        <a href="/register" class="text-xs font-bold uppercase tracking-widest text-slate-400 hover:text-rose-600 transition-colors">Open Account</a>
    </nav>

    <!-- Main Content -->
    <main class="flex-1 flex items-center justify-center p-6">
        <div id="login-container" class="w-full max-w-[440px]">
            
            <div class="glass-card rounded-[2.5rem] p-8 md:p-12">
                <div class="text-center mb-10">
                    <div class="w-16 h-16 bg-rose-50 text-rose-600 rounded-2xl flex items-center justify-center mx-auto mb-6">
                        <i data-lucide="shield-check" class="w-8 h-8"></i>
                    </div>
                    <h1 class="text-2xl font-extrabold tracking-tight mb-2">Secure Login</h1>
                    <p class="text-slate-500 text-sm font-medium">Access your digital wealth portal</p>
                </div>

                <form id="login-form" onsubmit="handleLogin(event)" class="space-y-5">
                    <!-- Identifier (Email or Phone) -->
                    <div class="space-y-2">
                        <label class="text-[10px] font-extrabold uppercase text-slate-400 ml-4 tracking-widest">Login ID</label>
                        <div class="relative group">
                            <input id="login-id" type="text" placeholder="Email or Mobile" required
                                class="w-full px-6 py-4 rounded-2xl border border-slate-200 outline-none transition-all input-focus-effect font-medium bg-white/50 focus:bg-white">
                            <i data-lucide="user" class="absolute right-6 top-1/2 -translate-y-1/2 text-slate-300 group-focus-within:text-rose-600 transition-colors w-5 h-5"></i>
                        </div>
                    </div>

                    <!-- Password -->
                    <div class="space-y-2">
                        <div class="flex justify-between items-center ml-4 mr-2">
                            <label class="text-[10px] font-extrabold uppercase text-slate-400 tracking-widest">Password</label>
                            <a href="#" class="text-[10px] font-extrabold uppercase text-rose-600 tracking-widest hover:underline">Forgot?</a>
                        </div>
                        <div class="relative group">
                            <input id="login-pass" type="password" placeholder="••••••••" required
                                class="w-full px-6 py-4 rounded-2xl border border-slate-200 outline-none transition-all input-focus-effect font-medium bg-white/50 focus:bg-white">
                            <button type="button" onclick="togglePass()" class="absolute right-6 top-1/2 -translate-y-1/2 text-slate-300 hover:text-slate-600 transition-colors">
                                <i id="eye-icon" data-lucide="eye" class="w-5 h-5"></i>
                            </button>
                        </div>
                    </div>

                    <!-- Status Message -->
                    <div id="login-error" class="view-hidden p-4 bg-rose-50 rounded-xl border border-rose-100 flex items-start space-x-3">
                        <i data-lucide="alert-circle" class="text-rose-600 w-5 h-5 mt-0.5"></i>
                        <p class="text-xs text-rose-800 font-semibold leading-relaxed" id="error-text">Invalid credentials. Please try again.</p>
                    </div>

                    <!-- Submit -->
                    <button type="submit" id="submit-btn" 
                        class="w-full bg-slate-900 text-white py-5 rounded-2xl font-bold text-lg shadow-xl hover:bg-rose-600 transition-all active:scale-[0.98] flex items-center justify-center group">
                        <span>Enter Dashboard</span>
                        <i data-lucide="chevron-right" class="ml-2 w-5 h-5 group-hover:translate-x-1 transition-transform"></i>
                    </button>
                </form>

                <div class="mt-8 pt-8 border-t border-slate-100 text-center">
                    <p class="text-xs text-slate-400 font-medium">Protected by NexGen Guard™ 256-bit encryption</p>
                </div>
            </div>

            <!-- Footer Links -->
            <div class="mt-8 flex justify-center space-x-6">
                <a href="#" class="text-[10px] font-bold text-slate-400 uppercase tracking-widest hover:text-slate-900 transition-colors">Security FAQ</a>
                <a href="#" class="text-[10px] font-bold text-slate-400 uppercase tracking-widest hover:text-slate-900 transition-colors">Privacy Policy</a>
                <a href="#" class="text-[10px] font-bold text-slate-400 uppercase tracking-widest hover:text-slate-900 transition-colors">Contact Support</a>
            </div>
        </div>
    </main>

    <script>
        lucide.createIcons();

        // Initial animation
        gsap.from('#login-container', { 
            opacity: 0, 
            y: 20, 
            duration: 0.8, 
            ease: "power3.out" 
        });

        function togglePass() {
            const passInput = document.getElementById('login-pass');
            const eyeIcon = document.getElementById('eye-icon');
            if (passInput.type === 'password') {
                passInput.type = 'text';
                eyeIcon.setAttribute('data-lucide', 'eye-off');
            } else {
                passInput.type = 'password';
                eyeIcon.setAttribute('data-lucide', 'eye');
            }
            lucide.createIcons();
        }

        async function handleLogin(e) {
            e.preventDefault();
            const btn = document.getElementById('submit-btn');
            const errorBox = document.getElementById('login-error');
            const loginId = document.getElementById('login-id').value;
            const loginPass = document.getElementById('login-pass').value;

            // UI Reset
            errorBox.classList.add('view-hidden');
            document.querySelectorAll('input').forEach(i => i.classList.remove('error-shake'));

            // Loading State
            btn.disabled = true;
            const originalContent = btn.innerHTML;
            btn.innerHTML = `<i data-lucide="loader-2" class="animate-spin w-6 h-6"></i>`;
            lucide.createIcons();

            try {
                const response = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        username: loginId,
                        password: loginPass
                    })
                });

                const result = await response.json();

                if (response.ok && result.success) {
                    // Success Transition
                    gsap.to('#login-container', { 
                        opacity: 0, 
                        scale: 0.95, 
                        duration: 0.4, 
                        onComplete: () => {
                            window.location.href = result.redirectUrl || '/dashboard';
                        } 
                    });
                } else {
                    throw new Error(result.message || "Authentication failed");
                }

            } catch (err) {
                // Error Presentation
                btn.disabled = false;
                btn.innerHTML = originalContent;
                lucide.createIcons();
                
                errorBox.classList.remove('view-hidden');
                document.getElementById('error-text').innerText = err.message;
                
                document.getElementById('login-id').classList.add('error-shake');
                document.getElementById('login-pass').classList.add('error-shake');
                
                gsap.from(errorBox, { x: 10, opacity: 0, duration: 0.3 });
            }
        }

        // Live reset on typing
        document.querySelectorAll('input').forEach(input => {
            input.addEventListener('input', () => {
                input.classList.remove('error-shake');
                document.getElementById('login-error').classList.add('view-hidden');
            });
        });
    </script>
</body>
</html>