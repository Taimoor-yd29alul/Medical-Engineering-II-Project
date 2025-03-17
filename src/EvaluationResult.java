package project;

public class EvaluationResult {

    double specificity;
    double sensitivity;

    public EvaluationResult ( double specificity , double sensitivity ){
        this.specificity = specificity;
        this.sensitivity = sensitivity;

    }
    public double getSpecificity (){
        return this.specificity;

    }
    public double getSensitivity (){
        return this.sensitivity;

    }
}
