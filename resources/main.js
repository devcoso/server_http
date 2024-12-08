document.addEventListener('DOMContentLoaded', () => {
    //Obtener elemetos
    const fileZone = document.getElementById('dropzone-file');
    const fileName = document.getElementById('file-name');
    const boton = document.getElementById('btn-submit');
    const btnRefresh = document.getElementById('btn-refresh');  
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
        //Leer archivo
        const reader = new FileReader();
        const file = fileZone.files[0];
        reader.readAsArrayBuffer(file);

        //Loader
        Swal.fire({
            title: 'Subiendo archivo...',
            showConfirmButton: false,
            willOpen: () => {
                Swal.showLoading();
            },
        });
        boton.disabled = true;

        reader.onload = () => {
            const buffer = reader.result;
            fetch(`/upload/${file.name}`, {
                method: 'POST',
                body: buffer,
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
                            getFiles();
                            //Limpiar input file
                            fileZone.value = '';
                            fileName.textContent = 'Añade otro archivo';
                            //Quitar loader
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

    btnRefresh.addEventListener('click', () => {
        getFiles();
    })

    //Obtener lista de archivos
    getFiles();

    function getFiles() {
        fetch('/list')
        .then(response => {
            if (response.ok) {
                response.json().then(data => {
                    const files = data;
                    list.innerHTML = '';

                    files.forEach((file, i) => {
                        file.size = parseInt(file.size);
                        file.size = file.size < 1024
                            ? `${file.size} B`
                            : file.size < 1024 * 1024
                                ? `${(file.size / 1024).toFixed(2)} KB`
                                : `${(file.size / 1024 / 1024).toFixed(2)} MB`;
                        const element = document.createElement('li');
                        const div = document.createElement('div');
                        div.classList.add('flex', 'flex-col' ,'md:flex-row', 'items-center', 'justify-between', 'my-1');
                        const a = document.createElement('a');
                        a.classList.add('text-lg', 'w-full', 'break-words', 'block', 'font-bold', 'hover:text-blue-500');
                        a.href = `/${file.name}`;
                        a.target = '_blank';
                        a.textContent = file.name;
                        const span = document.createElement('span');
                        span.classList.add('font-normal', 'text-sm', 'ml-2');
                        span.textContent = file.size;
                        const input = document.createElement('input');
                        input.hidden = true;
                        input.id = `file-name-${i}`;
                        input.type = 'text';
                        input.value = file.name;
                        const div2 = document.createElement('div');
                        div2.classList.add('flex', 'items-center', 'justify-between', 'space-x-2');
                        const buttonEdit = document.createElement('button');
                        buttonEdit.classList.add('bg-blue-500', 'hover:bg-blue-700', 'text-white', 'font-bold', 'py-2', 'px-4', 'rounded');
                        buttonEdit.textContent = 'Editar';
                        const buttonDelete = document.createElement('button');
                        buttonDelete.classList.add('bg-red-500', 'hover:bg-red-700', 'text-white', 'font-bold', 'py-2', 'px-4', 'rounded');
                        buttonDelete.textContent = 'Eliminar';
                        a.appendChild(span);
                        div.appendChild(a);
                        div.appendChild(input);
                        div2.appendChild(buttonEdit);
                        div2.appendChild(buttonDelete);
                        div.appendChild(div2);
                        element.appendChild(div);
                        list.appendChild(element);

                        // Cambiar archivo contenido
                        buttonEdit.addEventListener('click', () => {
                            const newName = document.getElementById(`file-name-${i}`).value;
                            Swal.fire({
                                title: 'Cambiar nombre de archivo',
                                input: 'text',
                                inputValue: newName,
                                showCancelButton: true,
                                confirmButtonText: 'Cambiar',
                                confirmButtonColor: '#4caf50',
                                cancelButtonText: 'Cancelar',
                                showLoaderOnConfirm: true,
                                preConfirm: (newName) => {
                                    return fetch(`/rename/${file.name}/${newName}`, {
                                        method: 'PUT',
                                    })
                                        .then(response => {
                                            if (!response.ok) {
                                                throw new Error(response.statusText);
                                            }
                                            return response.json();
                                        })
                                        .catch(error => {
                                            Swal.showValidationMessage(`Error: ${error}`);
                                        });
                                },
                            }).then(result => {
                                if (result.isConfirmed) {
                                    Swal.fire({
                                        toast: true,
                                        position: 'top-end',
                                        icon: 'success',
                                        title: result.value.message,
                                        showConfirmButton: false,
                                        timer: 3000,
                                        timerProgressBar: true,
                                    });
                                    getFiles();
                                }
                            });
                        });

                        // Eliminar archivo
                        buttonDelete.addEventListener('click', () => {
                            Swal.fire({
                                title: '¿Estás seguro de eliminar este archivo?',
                                text: 'No podrás recuperar el archivo una vez eliminado',
                                icon: 'warning',
                                showCancelButton: true,
                                confirmButtonText: 'Eliminar',
                                confirmButtonColor: '#d33',
                                cancelButtonText: 'Cancelar',
                                showLoaderOnConfirm: true,
                                preConfirm: () => {
                                    return fetch(`/delete/${file.name}`, {
                                        method: 'DELETE'
                                    })
                                        .then(response => {
                                            if (!response.ok) {
                                                throw new Error(response.statusText);
                                            }
                                            return response.json();
                                        })
                                        .catch(error => {
                                            Swal.showValidationMessage(`Error: ${error}`);
                                        });
                                },
                            }).then(result => {
                                if (result.isConfirmed) {
                                    Swal.fire({
                                        toast: true,
                                        position: 'top-end',
                                        icon: 'success',
                                        title: result.value.message,
                                        showConfirmButton: false,
                                        timer: 3000,
                                        timerProgressBar: true,
                                    });
                                    getFiles();
                                }
                            });
                        });

                    });
                });
            } else {
                response.json().then(data => {
                    console.error(data.message);
                });
            }
            })
            .catch(error => {
                console.error(error);
            });
        }
})