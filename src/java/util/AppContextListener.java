package util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import service.RecruitmentService;

/**
 * Application context listener để thực hiện các tác vụ khởi tạo và dọn dẹp khi ứng dụng khởi động hoặc kết thúc
 */
@WebListener
public class AppContextListener implements ServletContextListener {
    
    private static final Logger logger = Logger.getLogger(AppContextListener.class.getName());
    private ScheduledExecutorService scheduler;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.log(Level.INFO, "Ứng dụng Club Management System đang khởi động...");
        
        // Đồng bộ trạng thái các vòng tuyển lúc khởi động
        try {
            RecruitmentService recruitmentService = new RecruitmentService();
            int updatedCount = recruitmentService.syncAllStageStatus();
            logger.log(Level.INFO, "Đã đồng bộ {0} trạng thái vòng tuyển khi khởi động", updatedCount);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Lỗi khi đồng bộ trạng thái vòng tuyển: " + e.getMessage(), e);
        }
        
        // Thiết lập định thời đồng bộ tự động mỗi 30 phút
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                RecruitmentService recruitmentService = new RecruitmentService();
                int updatedCount = recruitmentService.syncAllStageStatus();
                logger.log(Level.INFO, "Đồng bộ định kỳ: Đã cập nhật {0} trạng thái vòng tuyển", updatedCount);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Lỗi khi đồng bộ định kỳ trạng thái vòng tuyển: " + e.getMessage(), e);
            }
        }, 30, 30, TimeUnit.MINUTES);
        
        logger.log(Level.INFO, "Đã thiết lập đồng bộ tự động trạng thái vòng tuyển mỗi 30 phút");
    }
    
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Dọn dẹp tài nguyên
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            logger.log(Level.INFO, "Đã dừng lịch đồng bộ trạng thái vòng tuyển");
        }
        logger.log(Level.INFO, "Ứng dụng Club Management System đang kết thúc...");
    }
}