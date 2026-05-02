/* ============================================================
   IHEC Ride — Utilitaires JS communs (sans framework)
   ============================================================
   API_BASE pointe vers le backend PHP (WAMP / Apache port 80).
   Modifier ci-dessous si le projet n'est pas dans C:\wamp64\www\ihec-ride.
*/

const API_BASE = 'http://localhost/ihec-ride/backend-php';

/* ---------- Gestion du localStorage auth ---------- */
const Auth = {
    save(data) {
        localStorage.setItem('ihecride_token', data.token || '');
        localStorage.setItem('ihecride_refresh', data.refresh || '');
        localStorage.setItem('ihecride_user', JSON.stringify({
            id: data.userId,
            role: data.role,
            prenom: data.prenom,
            nom: data.nom,
            email: data.email
        }));
    },
    user() {
        try { return JSON.parse(localStorage.getItem('ihecride_user') || 'null'); }
        catch (e) { return null; }
    },
    token() { return localStorage.getItem('ihecride_token') || ''; },
    role()  { const u = Auth.user(); return u ? u.role : null; },
    logout() {
        localStorage.removeItem('ihecride_token');
        localStorage.removeItem('ihecride_refresh');
        localStorage.removeItem('ihecride_user');
        window.location.href = '/frontend/login.html';
    },
    isLogged() { return !!Auth.token(); },
    requireRole(role) {
        const u = Auth.user();
        if (!u) { window.location.href = '/frontend/login.html'; return; }
        if (u.role !== role) { window.location.href = '/frontend/login.html'; }
    }
};

/* ---------- Parsing des réponses URL-encoded ---------- */
function parseFormEncoded(text) {
    const obj = {};
    if (!text) return obj;
    text.split('&').forEach(pair => {
        const [k, v] = pair.split('=');
        if (k) obj[decodeURIComponent(k)] = decodeURIComponent((v || '').replace(/\+/g,' '));
    });
    return obj;
}

/* ---------- Parsing des réponses pseudo-CSV ("|" séparées) ---------- */
function parsePipeTable(text) {
    if (!text || !text.trim()) return [];
    const lines = text.trim().split('\n');
    const headers = lines[0].split('|');
    const rows = [];
    for (let i = 1; i < lines.length; i++) {
        const cols = lines[i].split('|');
        const row = {};
        headers.forEach((h, idx) => row[h.trim()] = cols[idx] || '');
        rows.push(row);
    }
    return rows;
}

/* ---------- Wrappers fetch ----------
   Le backend PHP renvoie systématiquement du JSON. Les anciens formats
   (URL-encoded, pseudo-CSV) restent supportés en fallback pour la
   rétro-compatibilité avec d'éventuelles routes non migrées. */
async function apiPost(path, params) {
    const body = new URLSearchParams(params).toString();
    let res;
    try {
        res = await fetch(API_BASE + path, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                'Accept': 'application/json',
                'Authorization': 'Bearer ' + Auth.token()
            },
            body
        });
    } catch (e) {
        throw new Error("Serveur inaccessible. Vérifiez qu'Apache (WAMP) est démarré sur " + API_BASE + ".");
    }
    return await parseResponse(res);
}

async function apiGet(path, params = {}) {
    const qs = new URLSearchParams(params).toString();
    const url = API_BASE + path + (qs ? '?' + qs : '');
    let res;
    try {
        res = await fetch(url, {
            headers: {
                'Accept': 'application/json',
                'Authorization': 'Bearer ' + Auth.token()
            }
        });
    } catch (e) {
        throw new Error("Serveur inaccessible. Vérifiez qu'Apache (WAMP) est démarré sur " + API_BASE + ".");
    }
    return await parseResponse(res);
}

async function parseResponse(res) {
    const text = await res.text();
    const ct = (res.headers.get('Content-Type') || '').toLowerCase();
    if (ct.includes('application/json')) {
        try { return text ? JSON.parse(text) : {}; }
        catch (e) { throw new Error("Réponse JSON invalide du serveur."); }
    }
    if (text.includes('|') && text.includes('\n')) return parsePipeTable(text);
    return parseFormEncoded(text);
}

/* ---------- Notifications toast ---------- */
function toast(msg, type = 'info') {
    const div = document.createElement('div');
    div.className = 'toast toast-' + type;
    div.textContent = msg;
    document.body.appendChild(div);
    setTimeout(() => { div.style.opacity = '0'; setTimeout(() => div.remove(), 300); }, 3000);
}

/* ---------- Sidebar utilitaires ---------- */
function renderSidebar(containerId, role, currentPage) {
    const u = Auth.user();
    const menus = {
        PASSAGER: [
            { href: 'home.html',          label: 'Accueil' },
            { href: 'trajets.html',       label: 'Trajets disponibles' },
            { href: 'reservations.html',  label: 'Mes réservations' },
            { href: 'messagerie.html',    label: 'Messagerie' },
            { href: 'groupe.html',        label: 'Groupes' },
            { href: 'notifications.html', label: 'Notifications' },
            { href: 'help.html',          label: 'Aide' },
        ],
        CONDUCTEUR: [
            { href: 'home.html',              label: 'Tableau de bord' },
            { href: 'creer-trajet.html',      label: 'Créer un trajet' },
            { href: 'demandes.html',          label: 'Demandes reçues' },
            { href: 'passagers.html',         label: 'Passagers acceptés' },
            { href: 'messagerie.html',        label: 'Messagerie' },
            { href: 'groupe.html',            label: 'Groupe du trajet' },
            { href: 'evaluations.html',       label: 'Évaluations' },
            { href: 'notifications.html',     label: 'Notifications' },
            { href: 'help.html',              label: 'Aide' },
        ],
        ADMIN: [
            { href: 'home.html',              label: 'Tableau de bord' },
            { href: 'passagers.html',         label: 'Passagers' },
            { href: 'conducteurs.html',       label: 'Conducteurs' },
            { href: 'trajets.html',           label: 'Trajets' },
            { href: 'reclamations.html',      label: 'Réclamations' },
            { href: 'messagerie.html',        label: 'Messagerie globale' },
            { href: 'notifications.html',     label: 'Notifications' },
        ]
    };
    const items = menus[role] || [];
    const initials = u ? (u.prenom[0] + u.nom[0]).toUpperCase() : '??';
    const html = `
        <div class="brand">
            <img src="../assets/images/logo.jpeg" alt="IHEC Ride"/>
            <div class="brand-text">IHEC Ride<small>Covoiturage</small></div>
        </div>
        <div class="user-card">
            <div class="user-avatar">${initials}</div>
            <div class="user-info">
                <div class="name">${u ? u.prenom + ' ' + u.nom : 'Invité'}</div>
                <div class="role">${role || ''}</div>
            </div>
        </div>
        <nav>
            ${items.map(i => `
                <a href="${i.href}" class="${i.href === currentPage ? 'active' : ''}">
                    ${i.label}
                </a>`).join('')}
        </nav>
        <div class="logout-btn" onclick="Auth.logout()">Déconnexion</div>
    `;
    document.getElementById(containerId).innerHTML = html;
}

/* ---------- Formatage date FR ---------- */
function formatDate(d) {
    if (!d) return '';
    const dt = new Date(d.replace(' ', 'T'));
    if (isNaN(dt)) return d;
    return dt.toLocaleString('fr-FR', {
        day: '2-digit', month: 'short',
        hour: '2-digit', minute: '2-digit'
    });
}

/* ---------- SOS ---------- */
function triggerSOS() {
    if (!confirm('Confirmer l\'envoi d\'une alerte SOS à l\'administration ?')) return;
    navigator.geolocation.getCurrentPosition(async pos => {
        const r = await apiPost('/api/sos', {
            latitude: pos.coords.latitude,
            longitude: pos.coords.longitude
        });
        toast(r.message || 'Alerte SOS envoyée', (r.success === true || r.success === 'true') ? 'success' : 'danger');
    }, () => toast('Impossible d\'obtenir votre position', 'danger'));
}
