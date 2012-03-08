package net.wigis.yun;
/**
 * HashMap compatible Pair class
 * 
 */
@SuppressWarnings("rawtypes")
public class Pair<L, R> implements Comparable{
    private L first;
    private R second;

    public Pair(L first, R second) {
        this.first = first;
        this.second = second;
    }

    public int hashCode() {
        int hashFirst = first != null ? first.hashCode() : 0;
        int hashSecond = second != null ? second.hashCode() : 0;

        return (hashFirst + hashSecond) * hashSecond + hashFirst;
    }

    public boolean equals(Object other) {
        if (other instanceof Pair) {
                Pair otherPair = (Pair) other;
                return 
                ((  this.first == otherPair.first ||
                        ( this.first != null && otherPair.first != null &&
                          this.first.equals(otherPair.first))) &&
                 (      this.second == otherPair.second ||
                        ( this.second != null && otherPair.second != null &&
                          this.second.equals(otherPair.second))) );
        }

        return false;
    }

    public String toString()
    { 
           return "(" + first + ", " + second + ")"; 
    }

    public L getFirst() {
        return first;
    }

    public void setFirst(L first) {
        this.first = first;
    }

    public R getSecond() {
        return second;
    }

    public void setSecond(R second) {
        this.second = second;
    }

	@Override
	@SuppressWarnings("unchecked")
	public int compareTo(Object other) throws ClassCastException{
		// TODO Auto-generated method stub
		if(!(other instanceof Pair)){
			throw new ClassCastException("A Pair object expected.");
		}
		if((this.first instanceof Integer) && (this.second instanceof Integer)){
			//compare by larger sum
			Integer mSum = (Integer)(this.first) + (Integer)(this.second);
			Integer oSum =((Integer)(((Pair)other).getFirst()) + (Integer)((Pair)other).getSecond());
			int compareSum = mSum.compareTo(oSum);
			if(compareSum != 0)
				return compareSum;
		}
		
		int compareFirst = ((Comparable)this.first).compareTo(((Pair)other).getFirst());
		if(compareFirst != 0 ){
			return compareFirst;
		}else{
			return ((Comparable)this.second).compareTo(((Pair)other).getSecond());
		}
	}
}
