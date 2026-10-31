(function () {
    var poem = document.getElementById('poem');
    if (!poem) return;

    var id = poem.dataset.id;
    var ctx = poem.dataset.ctx;
    var csrf = document.querySelector('meta[name=csrf]').content;

    function post(url) {
        return fetch(url, { method: 'POST', headers: { 'X-CSRF-Token': csrf } })
            .then(function (r) { return r.json().then(function (d) { return { ok: r.ok, data: d }; }); })
            .then(function (x) {
                if (!x.ok) { alert(x.data.error || 'Ошибка'); return null; }
                return x.data;
            })
            .catch(function () { alert('Ошибка сети'); return null; });
    }

    var like = document.getElementById('like-btn');
    if (like) {
        like.addEventListener('click', function () {
            post(ctx + '/api/like?id=' + id).then(function (d) {
                if (!d) return;
                like.classList.toggle('on', d.active);
                document.getElementById('like-count').textContent = d.count;
            });
        });
    }

    var fav = document.getElementById('fav-btn');
    if (fav) {
        fav.addEventListener('click', function () {
            post(ctx + '/api/favorite?id=' + id).then(function (d) {
                if (d) fav.classList.toggle('on', d.active);
            });
        });
    }

    function render(stats) {
        poem.querySelectorAll('.line').forEach(function (el) {
            el.classList.remove('mine');
            el.querySelector('.cnt').textContent = '';
        });
        stats.forEach(function (s) {
            var el = poem.querySelector('.line[data-line="' + s.lineNo + '"]');
            if (!el) return;
            el.querySelector('.cnt').textContent = s.count;
            if (s.mine) el.classList.add('mine');
        });
    }

    poem.addEventListener('click', function (e) {
        if (poem.dataset.can !== 'true') return;
        var line = e.target.closest('.line');
        if (!line || line.querySelector('.text').textContent.trim() === '') return;
        post(ctx + '/api/highlight?id=' + id + '&line=' + line.dataset.line).then(function (stats) {
            if (stats) render(stats);
        });
    });
})();