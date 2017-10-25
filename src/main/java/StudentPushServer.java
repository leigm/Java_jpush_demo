import org.apache.log4j.Logger;

public class StudentPushServer {

    private static Logger logger = Logger.getLogger(StudentPushServer.class);

    public static void main(String[] args) {
        logger.info("====Test JPush Start===");

        //1 Contains the title and content
//        JPushTool jPushTool = new JPushTool("Title","Test Plush");

        //2 Contains the title
        JPushTool jPushTool = new JPushTool("This push contains only the title");

        //3 Send Push Message
        jPushTool.sendPushAll();

        logger.info("====Test JPush End===");

    }
}
