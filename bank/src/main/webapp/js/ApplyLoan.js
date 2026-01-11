/* --- LOAN APPLICATION LOGIC --- */

function openLoanModal(loanType) {
    document.getElementById('loan-application-modal').classList.remove('hidden');
    // Pre-select the loan type if clicked from a specific card
    if (loanType) {
        document.getElementById('loan-type').value = loanType;
    }
}

function closeLoanModal() {
    document.getElementById('loan-application-modal').classList.add('hidden');
    // Reset form state slightly
    document.getElementById('loan-msg').classList.add('hidden');
    document.getElementById('loan-submit-btn').disabled = false;
    document.getElementById('loan-submit-btn').innerHTML = `<span>Check Eligibility & Apply</span> <i class="fa-solid fa-arrow-right"></i>`;
}

function updateScoreColor(val) {
    const el = document.getElementById('score-val');
    if(val < 650) el.className = "text-xl font-bold text-red-600";
    else if(val < 750) el.className = "text-xl font-bold text-orange-500";
    else el.className = "text-xl font-bold text-green-600";
}

async function handleLoanApplication(e) {
    e.preventDefault();
    
    const btn = document.getElementById('loan-submit-btn');
    const msgBox = document.getElementById('loan-msg');
    
    // 1. Gather Data
    const userId = document.getElementById('loan-userId').value || document.getElementById('tx-userId').value;
    const type = document.getElementById('loan-type').value;
    const amount = document.getElementById('loan-amount').value;
    const tenure = document.getElementById('loan-tenure').value;
    const monthlyIncome = document.getElementById('loan-monthly-income').value; 
    
    // 2. Fetch the Auto-Calculated Debt from the Read-Only Field
    const debt = document.getElementById('loan-debt').value; 
    
    const score = document.getElementById('loan-score').value;

    // 3. Calculate Annual Income
    const calculatedAnnualIncome = parseFloat(monthlyIncome) * 12;

    const payload = {
        loanType: type,
        loanAmount: parseFloat(amount),
        tenureMonths: parseInt(tenure),
        annualIncome: calculatedAnnualIncome,
        monthlyIncome: parseFloat(monthlyIncome),
        ongoingDebt: parseFloat(debt), // This now uses the server-calculated value
        creditScore: parseInt(score),
        user: { 
            id: parseInt(userId) 
        }
    };

    // UI Feedback
    btn.disabled = true;
    btn.innerHTML = `<i class="fa-solid fa-circle-notch fa-spin"></i> Processing Application...`;
    msgBox.classList.add('hidden');

    try {
        const response = await fetch('/api/loans/apply', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        
        const result = await response.text();
        
        msgBox.classList.remove('hidden');
        if (result.startsWith("APPROVED")) {
            msgBox.className = "p-4 rounded-xl text-center text-sm font-bold bg-green-100 text-green-700 border border-green-200 flex items-center justify-center gap-2";
            msgBox.innerHTML = `<i class="fa-solid fa-circle-check text-xl"></i> <div>${result}</div>`;
            setTimeout(() => window.location.reload(), 2000);
        } else {
            msgBox.className = "p-4 rounded-xl text-center text-sm font-bold bg-red-50 text-red-600 border border-red-100";
            msgBox.innerText = result;
            btn.disabled = false;
            btn.innerHTML = `<span>Try Again</span> <i class="fa-solid fa-rotate-right"></i>`;
        }
    } catch (err) {
        msgBox.classList.remove('hidden');
        msgBox.innerText = "Connection Error: " + err.message;
        btn.disabled = false;
        btn.innerHTML = `<span>Retry</span> <i class="fa-solid fa-rotate-right"></i>`;
    }
}