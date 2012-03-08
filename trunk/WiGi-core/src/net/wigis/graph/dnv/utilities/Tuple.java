package net.wigis.graph.dnv.utilities;

public class Tuple<L, M, R> implements Comparable{
    private L left;
    private M middle;
    private R right;

    public Tuple(L left, M middle, R right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }
    public int hashCode() {
        int hashLeft = left != null ? left.hashCode() : 0;
        int hashMiddle = middle != null ? middle.hashCode() : 0;
        int hashRight = right != null ? right.hashCode() : 0;
        int hashA = (hashLeft + hashMiddle) * hashMiddle + hashLeft;
        return (hashA + hashRight) * hashRight + hashA;
        //return (hashLeft + hashMiddle + hashRight) * hashMiddle + (hashLeft + hashRight) * hashRight + hashLeft;
    }

    public boolean equals(Object other) {
        if (other instanceof Tuple) {
        	Tuple otherTuple = (Tuple) other;
            return 
                ((this.left == otherTuple.left ||
                	( this.left != null && otherTuple.left != null &&
                	  this.left.equals(otherTuple.left))) &&
                 (this.middle == otherTuple.middle ||
                    ( this.middle != null && otherTuple.middle != null &&
                      this.middle.equals(otherTuple.middle))) &&
                 (this.right == otherTuple.right ||
                    ( this.right != null && otherTuple.right != null &&
                      this.right.equals(otherTuple.right))) );
        }

        return false;
    }

    public String toString()
    { 
           return "(" + left + ", " + middle + ", " + right + ")"; 
    }

    public L getLeft() {
        return left;
    }

    public void setLeft(L left) {
        this.left = left;
    }
    public M getMiddle(){
    	return middle;
    }
    public void setMiddle(M middle){
    	this.middle = middle;
    }

    public R getRight() {
        return right;
    }

    public void setRight(R right) {
        this.right = right;
    }
	
	@Override
	public int compareTo(Object other) throws ClassCastException{
		// TODO Auto-generated method stub
		if(!(other instanceof Tuple)){
			throw new ClassCastException("A Tuple object expected.");
		}
		if((this.left instanceof Integer) && (this.middle instanceof Integer) && (this.right instanceof Integer)){
			//compare by larger sum
			Integer mSum = (Integer)(this.left) + (Integer)(this.middle) + (Integer)(this.right);
			Integer oSum = (Integer)(((Tuple)other).getLeft()) + (Integer)(((Tuple)other).getMiddle()) + (Integer)(((Tuple)other).getRight());
			int compareSum = mSum.compareTo(oSum);
			if(compareSum != 0)
				return compareSum;
		}
		int compareLeft = ((Comparable)this.left).compareTo(((Tuple)other).getLeft());
		if(compareLeft != 0 ){
			return compareLeft;
		}else{
			int compareMiddle = ((Comparable)this.middle).compareTo(((Tuple)other).getMiddle());
			if(compareMiddle != 0){
				return compareMiddle;
			}
			return ((Comparable)this.right).compareTo(((Tuple)other).getRight());
		}
	}
}
