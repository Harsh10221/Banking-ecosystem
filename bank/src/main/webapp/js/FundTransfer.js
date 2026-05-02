/* * Fund Transfer Logic 
 * Handles Instant Account Verification & Money Transfer
 */

/* --- 1. INSTANT ACCOUNT VERIFICATION --- */
async function checkReceiver() {
    const accountNo = document.getElementById('tf-receiverAccount').value;
    const nameDisplay = document.getElementById('tf-receiverName');
    const checkBtn = document.getElementById('btn-check-receiver');
    const bankField = document.getElementById('tf-receiverBank');

    // Basic Client-Side Validation
    if (!accountNo || accountNo.length < 5) {
        nameDisplay.innerHTML = '<span class="text-red-500 flex items-center gap-1"><i class="fa-solid fa-circle-exclamation"></i> Invalid Account Number</span>';
        nameDisplay.classList.remove('hidden');
        return;
    }

    // Set Loading State
    const originalBtnText = checkBtn.innerHTML;
    checkBtn.innerHTML = '<i class="fa-solid fa-circle-notch fa-spin"></i>';
    checkBtn.disabled = true;
    
    try {
        // Call Backend API
        const response = await fetch('/account/check', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ accountNumber: accountNo })
        });

        const data = await response.json();

		if (response.ok) {
            // SUCCESS: Display Name in GREEN
			const verifiedName = data.payload || data.data || "User Found"; 
			    
			    nameDisplay.innerHTML = `
			        <span class="text-green-600 flex items-center gap-2 bg-green-50 px-2 py-1 rounded-lg border border-green-100">
			            <i class="fa-solid fa-circle-check"></i> 
			            Verified: <span class="font-bold uppercase tracking-wide">${verifiedName}</span>
			        </span>`;
            
            // Auto-fill Bank Name if empty (UX improvement)
            if(!bankField.value) {
                bankField.value = "NexGen"; 
            }
        } else {
            // ERROR: Display Message in RED
            nameDisplay.innerHTML = `<span class="text-red-500 flex items-center gap-1"><i class="fa-solid fa-circle-xmark"></i> ${data.message}</span>`;
        }
    } catch (error) {
        console.error("Verification Error:", error);
        nameDisplay.innerHTML = '<span class="text-red-500 flex items-center gap-1"><i class="fa-solid fa-wifi"></i> Connection Error</span>';
    } finally {
        // Reset Button State
        nameDisplay.classList.remove('hidden');
        checkBtn.innerHTML = originalBtnText;
        checkBtn.disabled = false;
    }
}

/* --- 2. RESET VERIFICATION ON INPUT --- */
function resetReceiverCheck() {
    const nameDisplay = document.getElementById('tf-receiverName');
    if(nameDisplay) {
        nameDisplay.innerHTML = '';
        nameDisplay.classList.add('hidden');
    }
}

/* --- 3. HANDLE FUND TRANSFER SUBMISSION --- */
async function handleTransfer(event) {
    event.preventDefault();

    const btn = document.getElementById('tf-btn');
    const msgBox = document.getElementById('tf-message');
    
    // Gather Form Data
    const payload = {
        senderAccountNo: document.getElementById('tf-senderAccount').value,
        amount: document.getElementById('tf-amount').value,
        type: "Debit",
        receiverAccountNumber: document.getElementById('tf-receiverAccount').value,
        receiverBank: document.getElementById('tf-receiverBank').value
    };

    // Self-Transfer Prevention
    if(payload.senderAccountNo === payload.receiverAccountNumber) {
         showTransferMessage("You cannot transfer money to your own account.", "error");
         return;
    }

    // Set Loading State
    const originalBtnText = btn.innerHTML;
    btn.innerHTML = '<i class="fa-solid fa-circle-notch fa-spin"></i> Processing Transfer...';
    btn.disabled = true;
    msgBox.classList.add('hidden');

    try {
        const response = await fetch('/api/transaction/transfermoney', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        // Safe JSON parsing (handles plain text errors if server crashes)
        const text = await response.text();
        let data;
        try { 
            data = JSON.parse(text); 
        } catch(e) { 
            data = { message: text }; 
        }

        if (response.ok) {
            // SUCCESS
            const tokenDisplay = data.token ? data.token.substring(0, 8) + '...' : 'Completed';
            showTransferMessage(`Transfer Successful! <br><span class="text-xs text-green-600 mt-1 block">Ref ID: ${tokenDisplay}</span>`, 'success');
            
            // Clear Form
            document.getElementById('transfer-form').reset();
            resetReceiverCheck();
            
            // Optional: Refresh page to update balance after delay
            setTimeout(() => location.reload(), 3000);
        } else {
            // FAILURE
            let errMsg = data.message || data.error || 'Transfer Failed';
            
            // Clean up common database errors for user display
            if(errMsg.toLowerCase().includes("constraint")) errMsg = "Transaction denied: Database constraint violation.";
            if(errMsg.toLowerCase().includes("unable to reach")) errMsg = "Central Banking Hub is currently offline.";
            
            showTransferMessage(errMsg, 'error');
        }
    } catch (error) {
        console.error("Transfer API Error:", error);
        showTransferMessage('Server unreachable. Please check your internet connection.', 'error');
    } finally {
        // Reset Button
        btn.innerHTML = originalBtnText;
        btn.disabled = false;
    }
}

/* --- 4. HELPER: SHOW MESSAGES --- */
function showTransferMessage(msg, type) {
    const box = document.getElementById('tf-message');
    
    // Remove old classes
    box.classList.remove('hidden', 'bg-green-50', 'text-green-700', 'border-green-200', 'bg-red-50', 'text-red-700', 'border-red-200');
    
    // Add new classes based on type
    if (type === 'success') {
        box.classList.add('bg-green-50', 'text-green-700', 'border-green-200');
        box.innerHTML = `<div class="flex flex-col items-center gap-1"><i class="fa-solid fa-circle-check text-xl"></i> <span>${msg}</span></div>`;
    } else {
        box.classList.add('bg-red-50', 'text-red-700', 'border-red-200');
        box.innerHTML = `<div class="flex flex-col items-center gap-1"><i class="fa-solid fa-circle-exclamation text-xl"></i> <span>${msg}</span></div>`;
    }
    
    box.classList.remove('hidden');
}