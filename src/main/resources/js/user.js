const postsList = document.querySelector('.posts-list');
let output = '';

const renderPosts = (posts) => {

    posts.forEach(element => {
        console.log(element);
        output += `

            <div class="card mt-4 col-md-6 bg-ligt">
                <div class="card-body">
                    <h5 class="card-title">${element.username}</h5>
                    <h6 class="card-subtitle mb-2 text-body-secondary">Card subtitle</h6>
                    <p class="card-text">${element.email}</p>
                    <a href="#" class="card-link">Edit</a>
                    <a href="#" class="card-link">Delete</a>
                </div>
            </div>

            `;
    });
    postsList.innerHTML = output;

};

const url = 'http://localhost:8080/api/users';

// Get - Read the posts
fetch(url)
    .then(res => res.json()) // Преобразует ответ в JSON
    .then(data => renderPosts(data)); // Выведет реальные данные
