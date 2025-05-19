
<%@page contentType="text/html" pageEncoding="UTF-8"%>


<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Trang Câu Lạc Bộ</title>
        <link rel="stylesheet" href="style/homepage.css">
    </head>
    <body>
        <header class="navbar">
            <%@include file="view/header.jsp" %>
        </header>
        
        <nav class="menu">
            <button class="active">Câu lạc bộ</button>
            <button>Sự kiện</button>
        </nav>

        <div class="filter-bar">
            Lọc <select name="filter">
                <option value="all">Tất cả</option>
                <option value="joined">Đã tham gia</option>
            </select>

        </div>

        <main class="club-list">
            <div class="club-card">
                <div class="club-image">Hình ảnh</div>
                <div class="club-info">
                    <h3>Câu lạc bộ A</h3>
                    <p>Mô tả về câu lạc bộ:</p>
                    <p>Chủ nhiệm hiện tại:</p>
                </div>
            </div>

            <div class="club-card">
                <div class="club-image">Hình ảnh</div>
                <div class="club-info">
                    <h3>Câu lạc bộ B</h3>
                    <p>Mô tả về câu lạc bộ:</p>
                    <p>Chủ nhiệm hiện tại:</p>
                </div>
            </div>
        </main>

        <footer class="pagination">
            <button class="active">1</button>
            <button>2</button>
            <button>3</button>
            <button>4</button>
            <button>5</button>
        </footer>
    </body>
</html>

</body>
</html>

