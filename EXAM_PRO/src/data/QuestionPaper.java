package data;

public class QuestionPaper {
	private String filePath;
	private String year;
	private String q;
	private String topicName;
	private int totalMarks;
	public String getQPPath() {
		return filePath;
	}
	public void setQPPath(String filePath) {
		this.filePath = filePath;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getQ() {
		return q;
	}
	public void setQ(String q) {
		this.q = q;
	}
	public String getTopicName() {
		return topicName;
	}
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	public int getTotalMarks() {
		return totalMarks;
	}
	public void setTotalMarks(int totalMarks) {
		this.totalMarks = totalMarks;
	}
	
	public String toString(){
		return topicName +"|"+year+"|"+q+"|"+totalMarks+" marks";
	}
	
	public String getMSPath(){
		return this.getQPPath().substring(0, this.getQPPath().lastIndexOf("Questions\\")) +"Questions\\MS_"+this.getYear().replace(" ", "")+"_"+this.getQ()+".pdf";
	}
	
	public String getERPath(){
		return this.getQPPath().substring(0, this.getQPPath().lastIndexOf("Questions\\")) +"Questions\\ER_"+this.getYear().replace(" ", "")+"_"+this.getQ()+".pdf";
	}
	

}
