
(function Show(){

   

    const cookie = document.cookie;  
     console.log(cookie)
    // 使用xhr向URL为： 127.0.0.1:8083发送GET请求，参数为cookie=上面获取的cookie  
    const xhr = new XMLHttpRequest();  
    xhr.open('GET', 'http://127.0.0.1:8083', true);  
    xhr.withCredentials = true; // 携带跨域cookie
    
    xhr.onload = function () {  
        if (xhr.status >= 200 && xhr.status < 300) {  
            console.log(xhr.responseText);  // 请求成功，打印返回的内容  
        } else {  
            console.error(xhr.statusText);  // 请求失败，打印错误信息  
        }  
    };  
    
    xhr.onerror = function () {  
        console.error('请求发生错误');
    };  
    
    // 将获取的cookie添加到请求头中  
    
     xhr.setRequestHeader('Cookie', cookie);  
    

    xhr.send();

    
})()

