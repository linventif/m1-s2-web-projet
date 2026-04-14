async function toggleKudo(workoutId) {
    const btn = document.getElementById(`kudo-btn-${workoutId}`);
    const icon = document.getElementById(`kudo-icon-${workoutId}`);
    const countSpan = document.getElementById(`kudo-count-${workoutId}`);
    
    // Safety check for CSRF meta tag
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;

    try {
        const response = await fetch(`/workouts/${workoutId}/kudo`, {
            method: 'POST',
            headers: {
                'X-CSRF-TOKEN': csrfToken,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const data = await response.json(); 
            
            countSpan.innerText = data.newCount;

            if (data.isKudoed) {
                btn.classList.add('text-error');
                icon.classList.replace('fill-none', 'fill-current');
            } else {
                btn.classList.remove('text-error');
                icon.classList.replace('fill-current', 'fill-none');
            }
        }
    } catch (error) {
        console.error("Kudo toggle failed:", error);
    }
}