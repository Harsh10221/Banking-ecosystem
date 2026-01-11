/**
 * EmiSchedule.js
 * Handles fetching EMI data and displaying it with a custom UI
 */

// Global state to track which EMI is being paid
let currentEmiId = null;
let currentEmiAmount = null;

// ==========================================
// 1. MISSING FUNCTIONS ADDED HERE
// ==========================================

/**
 * Opens the Schedule Modal and fetches data
 * Triggered by "View Schedule" or "Pay EMI" buttons on Dashboard
 */
async function openEmiModal(loanId) {
    const modal = document.getElementById('emi-modal');
    const pendingContainer = document.getElementById('pendingContainer'); // Ensure you add this ID in JSP if missing
    
    // 1. Show modal immediately
    if (modal) {
        modal.classList.remove('hidden');
    }

    // 2. Fetch EMI Data from Backend
    // MAKE SURE YOU HAVE THIS ENDPOINT IN YOUR CONTROLLER
    try {
        const response = await fetch(`/api/loans/${loanId}/emis`); 
        
        if (response.ok) {
            const emiData = await response.json();
            renderEmiSchedule(emiData);
        } else {
            console.error("Failed to load EMIs");
            if(pendingContainer) pendingContainer.innerHTML = '<p class="text-red-500 text-center">Failed to load schedule.</p>';
        }
    } catch (error) {
        console.error("Network error:", error);
    }
}

/**
 * Closes the Schedule Modal
 */
function closeEmiModal() {
    const modal = document.getElementById('emi-modal');
    if (modal) {
        modal.classList.add('hidden');
    }
}

// ==========================================
// 2. EXISTING LOGIC (Kept as is)
// ==========================================

function showPaymentModal(amount, emiId) {
    currentEmiId = emiId;
    currentEmiAmount = amount;
    
    const modalContent = document.querySelector('#paymentModal > div');
    
    // RESET THE UI: Always inject the fresh "Confirm" HTML structure
    // This fixes the issue where the modal gets stuck on the "Error" screen
    modalContent.innerHTML = `
        <div class="mb-4 flex items-center justify-center">
            <div class="flex h-12 w-12 items-center justify-center rounded-full bg-blue-100 text-blue-600">
                <i class="fa-solid fa-indian-rupee-sign text-xl"></i>
            </div>
        </div>
        <h3 class="text-center text-xl font-bold text-gray-800">Confirm Payment</h3>
        <p class="mt-2 text-center text-gray-600">
            Are you sure you want to pay <span class="font-bold text-gray-900">₹ ${amount}</span> for this EMI?
        </p>
        <div class="mt-6 flex gap-3">
            <button onclick="closePaymentModal()" class="flex-1 rounded-xl border border-gray-300 py-3 font-medium text-gray-700 hover:bg-gray-50">
                Cancel
            </button>
            <button id="confirmPayBtn" onclick="processEmiPayment()" class="flex-1 rounded-xl bg-blue-600 py-3 font-medium text-white hover:bg-blue-700">
                Confirm & Pay
            </button>
        </div>
    `;
    
    const modal = document.getElementById('paymentModal');
    if (modal) {
        modal.classList.remove('hidden');
        modal.classList.add('flex');
    }
}

function closePaymentModal() {
    const modal = document.getElementById('paymentModal');
    if (modal) {
        modal.classList.add('hidden');
        modal.classList.remove('flex');
    }
}


function renderEmiSchedule(emiData) {
    const pendingContainer = document.getElementById('emi-list-pending'); 
    const paidContainer = document.getElementById('emi-list-paid');

    if (!pendingContainer || !paidContainer) return;

    pendingContainer.innerHTML = '';
    paidContainer.innerHTML = '';
    
    emiData.forEach(emi => {
        // FIX: Check for different possible field names from backend
        const amount = emi.amount || emi.emiAmount || emi.installmentAmount || 0;
        const id = emi.id || emi.emiId;
        const status = emi.status || "PENDING";
        
        const isPaid = status === 'PAID';
        
        let dateStr = "N/A";
        if(emi.dueDate) {
             dateStr = new Date(emi.dueDate).toLocaleDateString('en-IN', { 
                day: 'numeric', month: 'short', year: 'numeric' 
            });
        }

        const html = `
            <div class="flex items-center justify-between p-4 bg-white border border-slate-100 rounded-xl shadow-sm hover:shadow-md transition-all">
                <div class="flex items-center gap-4">
                    <div class="w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0 ${isPaid ? 'bg-green-100 text-green-600' : 'bg-red-50 text-red-600'}">
                        <i class="fa-solid ${isPaid ? 'fa-check' : 'fa-indian-rupee-sign'}"></i>
                    </div>
                    <div>
                        <p class="text-sm font-bold text-slate-800">₹ ${amount}</p>
                        <p class="text-[10px] text-gray-400 font-medium">Due: ${dateStr}</p>
                    </div>
                </div>
                ${!isPaid ? `
                    <button onclick="showPaymentModal('${amount}', '${id}')" 
                            class="bg-slate-900 text-white text-xs px-4 py-2 rounded-lg hover:bg-black transition-colors font-bold uppercase tracking-wider">
                        Pay Now
                    </button>
                ` : '<span class="text-xs font-bold text-green-600 uppercase">Paid</span>'}
            </div>
        `;

        if (isPaid) {
            paidContainer.insertAdjacentHTML('beforeend', html);
        } else {
            pendingContainer.insertAdjacentHTML('beforeend', html);
        }
    });
}

async function processEmiPayment() {
    if (!currentEmiId) return;

    const confirmBtn = document.getElementById('confirmPayBtn');
    
    // 1. Show Loading State
    if(confirmBtn) {
        confirmBtn.disabled = true;
        confirmBtn.innerHTML = '<i class="fa-solid fa-circle-notch fa-spin"></i> Processing...';
    }

    try {
        const response = await fetch(`/api/loans/pay-emi/${currentEmiId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        const message = await response.text(); 
        const modalContent = document.querySelector('#paymentModal > div');

        if (response.ok && message.includes("SUCCESS")) {
            // SUCCESS: Show Green Checkmark & Reload
            modalContent.innerHTML = `
                <div class="flex flex-col items-center justify-center py-8">
                    <div class="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mb-4 animate-bounce">
                        <i class="fa-solid fa-check text-3xl text-green-600"></i>
                    </div>
                    <h3 class="text-xl font-bold text-slate-800">Payment Successful!</h3>
                    <p class="text-sm text-gray-500 mt-2">Updating your schedule...</p>
                </div>
            `;
            setTimeout(() => location.reload(), 2000);

        } else {
            // ERROR: Show Red X & Close Button (NO RELOAD)
            const cleanError = message.replace("ERROR:", "").trim();
            
            modalContent.innerHTML = `
                <div class="flex flex-col items-center justify-center py-6">
                    <div class="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mb-4 shake-animation">
                        <i class="fa-solid fa-circle-xmark text-3xl text-red-600"></i>
                    </div>
                    <h3 class="text-xl font-bold text-slate-800">Payment Failed</h3>
                    <p class="text-sm text-red-500 mt-2 text-center px-4 font-medium">
                        ${cleanError || "Something went wrong. Please try again."}
                    </p>
                    
                    <button onclick="closePaymentModal()" 
                            class="mt-6 px-6 py-2 bg-slate-100 text-slate-700 font-bold rounded-xl hover:bg-slate-200 transition-colors">
                        Close & Retry
                    </button>
                </div>
            `;
        }

    } catch (error) {
        console.error("Error processing payment:", error);
        
        // Network Error UI
        const modalContent = document.querySelector('#paymentModal > div');
        modalContent.innerHTML = `
            <div class="flex flex-col items-center justify-center py-6">
                <div class="w-16 h-16 bg-orange-100 rounded-full flex items-center justify-center mb-4">
                    <i class="fa-solid fa-wifi text-3xl text-orange-600"></i>
                </div>
                <h3 class="text-xl font-bold text-slate-800">Connection Error</h3>
                <p class="text-sm text-gray-500 mt-2 text-center">Unable to reach the server.</p>
                <button onclick="closePaymentModal()" 
                        class="mt-6 px-6 py-2 bg-slate-100 text-slate-700 font-bold rounded-xl hover:bg-slate-200">
                    Close
                </button>
            </div>
        `;
    }
}

// Attach event listener to the "Confirm & Pay" button
document.addEventListener('DOMContentLoaded', () => {
    const confirmBtn = document.getElementById('confirmPayBtn');
    if (confirmBtn) {
        confirmBtn.addEventListener('click', processEmiPayment);
    }
});