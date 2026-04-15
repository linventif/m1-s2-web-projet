if (typeof postCommentAjax !== 'function') {
    window.postCommentAjax = function(id) {
        const input = document.getElementById('comment-input-' + id);
        const content = input.value.trim();
        if (!content) return;

        const token = document.querySelector('meta[name="_csrf"]').content;
        const header = document.querySelector('meta[name="_csrf_header"]').content;

        fetch(`/workouts/${id}/comments`, {
            method: 'POST',
            headers: { 
                [header]: token, 
                'Content-Type': 'application/x-www-form-urlencoded',
                'Accept': 'text/html' 
            },
            body: new URLSearchParams({ 'content': content })
        })
        .then(res => {
            if (!res.ok) throw new Error('Erreur');
            return res.text();
        })
        .then(html => {
            const wrapper = document.getElementById('comment-wrapper-' + id);
            wrapper.outerHTML = html;
            
            // Optionnel : Rouvrir le collapse après le refresh si nécessaire
            // Le rafraîchissement réinitialise l'état de la checkbox.
            const newWrapper = document.getElementById('comment-wrapper-' + id);
            const checkbox = newWrapper.querySelector('input[type="checkbox"]');
            if(checkbox) checkbox.checked = true;
        })
        .catch(err => console.error('Erreur AJAX:', err));
    };
}

if (typeof deleteCommentAjax !== "function") {
    window.deleteCommentAjax = function(workoutId, commentId) {
        const token = document.querySelector('meta[name="_csrf"]').content;
        const header = document.querySelector('meta[name="_csrf_header"]').content;

        fetch(`/workouts/${workoutId}/comments/${commentId}/delete`, {
            method: "POST",
            headers: {
                [header]: token,
                Accept: "text/html"
            }
        })
            .then((res) => {
                if (!res.ok) throw new Error("Erreur");
                return res.text();
            })
            .then((html) => {
                const wrapper = document.getElementById("comment-wrapper-" + workoutId);
                if (!wrapper) return;
                wrapper.outerHTML = html;

                const newWrapper = document.getElementById("comment-wrapper-" + workoutId);
                const checkbox = newWrapper ? newWrapper.querySelector('input[type="checkbox"]') : null;
                if (checkbox) checkbox.checked = true;
            })
            .catch((err) => console.error("Erreur AJAX:", err));
    };
}
