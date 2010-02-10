/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package castadiva.TrafficRecords;

/**
 *
 * @author alvaro
 */
public class ExecutionRecord {
    private String sourceFolder;
    private String resultsFolder;
    private Integer runs;

    public String getResultsFolder() {
        return resultsFolder;
    }

    public void setResultsFolder(String resultsFolder) {
        this.resultsFolder = resultsFolder;
    }

    public Integer getRuns() {
        return runs;
    }

    public void setRuns(Integer runs) {
        this.runs = runs;
    }

    public String getSourceFolder() {
        return sourceFolder;
    }

    public void setSourceFolder(String sourceFolder) {
        this.sourceFolder = sourceFolder;
    }
}