package codegen;

import org.apache.log4j.BasicConfigurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cfcc.jaf.persistence.jaform.MapGenerator;

/**
 * @author Rengm
 */
public class OrmapGenerate {
	/**
	 * commons Logger for this class
	 */

	public static void main(String[] args) {
		genDto("codegen/Generator.xml");
	}

	private static void genDto(String fileDir) {
		BasicConfigurator.configure();
		try {
			long lBegin = System.currentTimeMillis();
			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(fileDir);
					
			MapGenerator generator = (MapGenerator) context
					.getBean("mapGenerator.ORM.GEN.ID");
			System.out.println("Dto在生成中，请稍等......");
			generator.init();
			generator.setEncode("UTF-8");
			generator.generate();
			
			long lEnd = System.currentTimeMillis();

			System.out.println("Process is OK,The Whole time=" + (lEnd - lBegin)/1000 + "s");
					
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
