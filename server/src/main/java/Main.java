import chess.*;
/*
To do:
Add file strucutre
Set up your starter code so that your server runs properly, and make sure the testing webpage loads.
Use your sequence diagrams and the class diagram at the top of this page to guide the decision for what classes you might need.
Create packages for where these classes will go, if you haven't already done so.
Pick one Web API endpoint and get it working end-to-end. We recommend starting with clear or register.
Create the classes you need to implement the endpoint.
Write a service test or two to make sure the service and data access parts of your code are working as you expect.
Make sure you can hit your endpoint from the test page on a browser or Curl. Verify the response is what you expect it to be.
Repeat this process for all other endpoints.
 */
public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
    }
}