import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ReadModel {
    private static HiddenMarkovModel trained;
    private static ReadModel model;


    public  HiddenMarkovModel getModel(){
        model = new ReadModel();
        trained = model.deserializeModel("trainedModel.ser");
        return trained;
    }

    public HiddenMarkovModel deserializeModel(String filename){
        HiddenMarkovModel trained = null;
        FileInputStream fileIn = null;
        ObjectInputStream objIn = null;

        try{
            fileIn = new FileInputStream(filename);
            objIn = new ObjectInputStream(fileIn);
            trained = (HiddenMarkovModel) objIn.readObject();
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            if(fileIn != null){
                try{
                    fileIn.close();
                } catch(IOException e){
                    e.printStackTrace();
                }
            }

            if(objIn != null){
                try{
                    objIn.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

        return trained;
    }


}
