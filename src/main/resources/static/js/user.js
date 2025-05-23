document.addEventListener('DOMContentLoaded', function() {
    // Получаем CSRF токен для logout
    fetch('/api/user')
        .then(response => {
            if (!response.ok) throw new Error('Network response was not ok');
            return response.json();
        })
        .then(user => {
            renderUserInfo(user);
            setupLogout();
        })
        .catch(error => console.error('Error:', error));
});

function renderUserInfo(user) {
    // 1. Заполняем шапку (email и роли)
    const rolesText = user.authorities.map(role =>
        role.authority.replace('ROLE_', '')).join(', ');

    document.getElementById('user-email-roles').innerHTML = `
        <span>${user.email} with roles: ${rolesText}</span>
    `;

    // 2. Заполняем CSRF токен для формы logout
    document.getElementById('csrf-token').value =
        document.querySelector('meta[name="_csrf"]').content;

    // 3. Создаем вкладки ролей
    const pillsTab = document.getElementById('v-pills-tab');
    const tabContent = document.getElementById('v-pills-tabContent');

    user.authorities.forEach((role, index) => {
        const roleName = role.authority.replace('ROLE_', '');

        // Создаем вкладку
        const pill = document.createElement('a');
        pill.className = `nav-link ${index === 0 ? 'active' : ''}`;
        pill.id = `v-pills-${role.authority}-tab`;
        pill.dataset.bsToggle = 'pill';
        pill.href = `#v-pills-${role.authority}`;
        pill.role = 'tab';
        pill.setAttribute('aria-controls', `v-pills-${role.authority}`);
        pill.setAttribute('aria-selected', index === 0 ? 'true' : 'false');
        pill.textContent = roleName;
        pillsTab.appendChild(pill);

        // Создаем контент для вкладки
        const pane = document.createElement('div');
        pane.className = `tab-pane fade ${index === 0 ? 'show active' : ''}`;
        pane.id = `v-pills-${role.authority}`;
        pane.role = 'tabpanel';
        pane.setAttribute('aria-labelledby', `v-pills-${role.authority}-tab`);

        if (role.authority === 'ROLE_USER') {
            pane.innerHTML = `
                <p class="h1">User information-page</p>
                <div class="card">
                    <div class="card-header p-2">
                        <p class="fw-bold fs-5 mb-0">About user</p>
                    </div>
                    <div class="card-body p-0 d-flex flex-column">
                        <div class="table-responsive pe-4 ps-4">
                            <table class="table table-striped w-100">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Name</th>
                                        <th>Email</th>
                                        <th>Role</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td>${user.id}</td>
                                        <td>${user.username}</td>
                                        <td>${user.email}</td>
                                        <td>${rolesText}</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            `;
        }
        tabContent.appendChild(pane);
    });
}

function setupLogout() {
    document.getElementById('logout-form').addEventListener('submit', function(e) {
        e.preventDefault();

        const csrfToken = document.getElementById('csrf-token').value;

        fetch('/logout', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-CSRF-TOKEN': csrfToken
            },
            body: `_csrf=${csrfToken}`
        }).then(() => window.location.href = '/login');
    });
}