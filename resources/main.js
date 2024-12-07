document.addEventListener('DOMContentLoaded', () => {
    //Obtener elemetos
    const fileZone = document.getElementById('dropzone-file');
    const fileName = document.getElementById('file-name');
    const boton = document.getElementById('btn-submit');
    const list = document.getElementById('file-list');

    //Obtener input file
    fileZone.addEventListener('change', () => {
        const file = fileZone.files[0];
        if (file) {
            fileName.textContent = file.name;
            boton.disabled = false;
        }
    })

    //Mandar post para crear archivo al dar click en el boton
    boton.addEventListener('click', () => {
        //Codificar el archivo en base64
        const reader = new FileReader();
        reader.readAsDataURL(fileZone.files[0]);
        reader.onload = () => {
            const base64 = reader.result.split(',')[1];
            console.log(base64);
            fetch(`/upload/${fileZone.files[0].name}`, {
                method: 'POST',
                body: base64
            })
                .then(response => {
                    if (response.ok) {
                        response.json().then(data => {
                            Swal.fire({
                                toast: true,
                                position: 'top-end',
                                icon: 'success',
                                title: data?.message,
                                showConfirmButton: false,
                                timer: 3000,
                                timerProgressBar: true,
                            });
                        });
                    } else {
                        response.json().then(data => {
                            Swal.fire({
                                toast: true,
                                position: 'top-end',
                                icon: 'error',
                                title: data?.message,
                                showConfirmButton: false,
                                timer: 3000,
                                timerProgressBar: true,
                            });
                        });
                    }
                })
                .catch(error => {
                    console.error(error);
                    Swal.fire({
                        toast: true,
                        position: 'top-end',
                        icon: 'error',
                        title: 'Error al subir archivo',
                        showConfirmButton: false,
                        timer: 3000,
                        timerProgressBar: true,
                    });
                });
        }
    })
    //Obtener lista de archivos

    //fake endpoint
    const files = [
        {
            name : 'archivo1.txt',
            size : '1.2MB',
        },
        {
            name : 'archivo2.pdf',
            size : '0.2MB',
        },
        {
            name : 'book.xls',
            size : '1.2MB',
        },
        {
            name : 'class1.java',
            size : '1.2MB',
        },
    ]

    showFiles(files);

    function showFiles (files) {
        //list.innerHTML = '';
        files.forEach((file, i) => {
            const element = document.createElement('li');
            element.innerHTML = `
                    <div class="flex items-center justify-between my-1">
                        <p class="text-lg font-bold">${file.name} <span class="font-normal text-sm">${file.size}</span></p>
                        <div class="flex items-center justify-between gap-2">
                            <input hidden id="file-name-${i}" type="text" value="${file.name}" />
                            <button id="edit-file-${i}" class="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">Editar</button>
                            <button id="delete-file-${i}" class="bg-red-500 hover:bg-red-700 text-white font-bold py-2 px-4 rounded">Eliminar</button>
                    </div>`;
            list.appendChild(element);
        })
    }

    //Editar archivo

    //Eliminar archivo
})