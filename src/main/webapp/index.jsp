<html>
<body>
    <h2>文件上传测试</h2>
    <from name="from" action="/manage/product/fileUpload.do" method="post" enctype="multipart/form-data">
        <input type="file" name="upload_file" >
        <input type="submit" value="上传文件"/>
    </from>
</body>
</html>
