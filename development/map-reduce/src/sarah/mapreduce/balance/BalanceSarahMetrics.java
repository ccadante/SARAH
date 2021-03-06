package sarah.mapreduce.balance;

import org.apache.hadoop.conf.Configuration;

import sarah.mapreduce.metrics.CountedLongMapReduceMetric;
import sarah.mapreduce.metrics.MapReduceSarahMetrics;
import sarah.metrics.CountedLongSarahMetric;
import sarah.metrics.SarahMetric;

public class BalanceSarahMetrics extends MapReduceSarahMetrics {

	// metrics generated by the balance tool
	public CountedLongMapReduceMetric recommendedNumberReducersForF;
	private SarahMetric<String> intervalFileForF;
	public SarahMetric<?>[] balanceMetrics = new SarahMetric<?>[2];
	
	private static BalanceSarahMetrics singleton;
	
	public BalanceSarahMetrics(Configuration conf) {
		super(conf);
		intervalFileForF = new SarahMetric<String>("sarah.%func.intervalfile","TotalOrderPartitioner interval file location");
		intervalFileForF.setValue("artifacts/balance/%func/interval-m-00001");
		recommendedNumberReducersForF = 
				new CountedLongMapReduceMetric(new CountedLongSarahMetric("sarah.%func.number.reducers","Recommended number of reducers"),"recommendedNumberReducersForF",conf);
		singleton = this;
		balanceMetrics[0] = recommendedNumberReducersForF;
		balanceMetrics[1] = intervalFileForF;
		
	}
	
	public static BalanceSarahMetrics get() {
		return singleton;
	}
}
