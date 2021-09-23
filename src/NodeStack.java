
public class NodeStack {
	private Object data;
	private NodeStack link;
	
	NodeStack(Object data){
		this.data=data;
		link=null;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public NodeStack getLink() {
		return link;
	}

	public void setLink(NodeStack link) {
		this.link = link;
	}
	
	
	
}
