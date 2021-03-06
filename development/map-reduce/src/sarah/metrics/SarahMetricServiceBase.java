package sarah.metrics;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public abstract class SarahMetricServiceBase implements SarahMetricService {

	public static String sarahTitle = "Sarah Statistics";
	private FileSystem fs;
	private String dotOfGeneration;
	


	public SarahMetricServiceBase(FileSystem fileSystem) {
		fs = fileSystem;
		dotOfGeneration = Calendar.getInstance().getTime().toString();
	}

	// A default version of save that writes XML to file system.
	// Subclasses are required to implement abstract methods asXML() and outputfile()
	@Override
	public void save() throws IOException {
		SarahMetrics.get().sarahToolName.setValue(getToolName());
		SarahMetrics.get().sarahToolVersion.setValue(getToolVersion());
		SarahMetrics.get().sarahDateOfGeneration.setValue(dotOfGeneration);
		FSDataOutputStream out = fs.create(outputFilePath());
		out.writeBytes(asXML());
		out.close();
	}

	abstract protected String asXML() throws IOException;
	abstract protected Path outputFilePath();

	// Helper methods that subclasses can use in implementation of save() and print()
	
	protected void printTool(PrintStream out) throws IOException {
		SarahMetrics.get().sarahToolName.setValue(getToolName());
		SarahMetrics.get().sarahToolVersion.setValue(getToolVersion());
		SarahMetrics.get().sarahDateOfGeneration.setValue(dotOfGeneration);
		out.println("\n#"+sarahTitle);
	}
	
	protected void printMetrics(PrintStream out, String title, SarahMetric<?>[] inputDataSetMetrics) {
		out.println("\n\n\n# "+title);
		for (SarahMetric<?> metric: inputDataSetMetrics) {
			printMetric(out,metric);
		}
	}

	protected void printFunctionMetrics(PrintStream out, String title, SarahMetric<?>[] functionMetrics, boolean addSarahFunctionsMetric) {
		out.println("\n\n# "+title);
		if (addSarahFunctionsMetric) {
			SarahMetric<String[]> metric = SarahMetrics.sarahFunctions;
			out.println("\n# "+metric.description);
			out.println(metric.name+"="+toCommaSeparated(metric.getValue()));
		}
		for (String function : SarahMetrics.sarahFunctions.getValue()) {
			printMetrics(out,"    "+function,functionMetricsForF(function, functionMetrics));
		}
	}
	
	protected void functionMetricsAsXML(StringBuilder result, String title, SarahMetric<?>[] functionMetrics, boolean addSarahFunctionsMetric) {
		result.append("\n    <!-- "+title+" -->\n");
		if (addSarahFunctionsMetric) {
			SarahMetric<String[]> metric = SarahMetrics.sarahFunctions;
			result.append("    <property>\n");
			result.append("        <!-- "+metric.description+" -->");
			result.append("\n        <name>"+metric.name+"</name>\n        <value>");
			result.append(toCommaSeparated(metric.getValue())+"</value>\n    </property>\n");
		}
		for (String function : SarahMetrics.sarahFunctions.getValue()) {
			metricsAsXML(result,function,functionMetricsForF(function, functionMetrics));
		}		
	}

	protected void metricsAsXML(StringBuilder result, String title, SarahMetric<?>[] metrics) {
		result.append("\n    <!-- "+title+" -->\n");
		for (SarahMetric<?> metric: metrics) {
			metricAsXML(result,metric);
		}
	}

	
	// Methods only known to the base class, used by helpers
	
	private void metricAsXML(StringBuilder result,SarahMetric<?> metric) {
		result.append("    <property>\n");
		result.append("        <!-- "+metric.description+" -->");
		result.append("\n        <name>"+metric.name+"</name>\n        <value>");
		result.append(metric.getValue()+"</value>\n    </property>\n");
	}
	
	@SuppressWarnings("unchecked")
	private SarahMetric<Object>[] functionMetricsForF(String function, SarahMetric<?>[] metrics) {
		SarahMetric<Object>[] metricsForF = new SarahMetric[metrics.length];
		short i = 0;
		for (SarahMetric<?> metric: metrics) {
			metricsForF[i]=new SarahMetric<Object>(
										SarahMetrics.get().stringForF(metric.name,function),
										SarahMetrics.get().stringForF(metric.description,function));
			metricsForF[i++].setValue(metric.getValue(function));
		}
		return metricsForF;
	}
	
	private void printMetric(PrintStream out, SarahMetric<?> metric) {
		out.println("\n# "+metric.description);
		out.println(metric.name+"="+metric.getValue());
	}
	
	private String toCommaSeparated(String[] functionNames) {
		String result="";
		int numberFunctionNames = functionNames.length;
		for (int i=0; i<numberFunctionNames; i++) {
			result=result+functionNames[i];
			if (i!=numberFunctionNames-1) {
				result=result+",";
			}
		}
		return result;
	}
}
