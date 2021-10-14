package iatools;

public class Produto {
	private String procod;
	private String prodes;
	private String seccod;
	private String grpcod;
	private String sgrcod;
	private float proprccst;
	private float proprcvdavar;
	private float proprcvda2;
	private float proprcvda3;
	private float proprc1;
	private String proncm;
	private String procest;
	private String protaba;
	private float estatu;
	private String procodaux;

	public String getSeccod() {
		return seccod;
	}
	public Produto(String procod, String prodes, String seccod, String grpcod, String sgrcod, float proprccst,
			float proprcvdavar, float proprcvda2, float proprcvda3, float proprc1, String proncm, String procest,
			String protaba, float estatu, String procodaux) {
		super();
		this.procod = procod;
		this.prodes = prodes;
		this.seccod = seccod;
		this.grpcod = grpcod;
		this.sgrcod = sgrcod;
		this.proprccst = proprccst;
		this.proprcvdavar = proprcvdavar;
		this.proprcvda2 = proprcvda2;
		this.proprcvda3 = proprcvda3;
		this.proprc1 = proprc1;
		this.proncm = proncm;
		this.procest = procest;
		this.protaba = protaba;
		this.estatu = estatu;
		this.procodaux = procodaux;
	}
	public void setSeccod(String seccod) {
		this.seccod = seccod;
	}
	public String getGrpcod() {
		return grpcod;
	}
	public void setGrpcod(String grpcod) {
		this.grpcod = grpcod;
	}
	public String getSgrcod() {
		return sgrcod;
	}
	public void setSgrcod(String sgrcod) {
		this.sgrcod = sgrcod;
	}
	public float getProprcvda2() {
		return proprcvda2;
	}
	public void setProprcvda2(float proprcvda2) {
		this.proprcvda2 = proprcvda2;
	}
	public float getProprcvda3() {
		return proprcvda3;
	}
	public void setProprcvda3(float proprcvda3) {
		this.proprcvda3 = proprcvda3;
	}

	public Produto() {
		
	}

	public String getProcod() {
		return procod;
	}

	public void setProcod(String procod) {
		this.procod = procod;
	}

	public String getProdes() {
		return prodes;
	}

	public void setProdes(String prodes) {
		this.prodes = prodes;
	}

	public float getProprccst() {
		return proprccst;
	}

	public void setProprccst(float proprccst) {
		this.proprccst = proprccst;
	}

	public float getProprc1() {
		return proprc1;
	}

	public void setProprc1(float proprc1) {
		this.proprc1 = proprc1;
	}

	public String getProtaba() {
		return protaba;
	}

	public void setProtaba(String protaba) {
		this.protaba = protaba;
	}

	public String getProcodaux() {
		return procodaux;
	}

	public void setProcodaux(String procodaux) {
		this.procodaux = procodaux;
	}

	public String getProncm() {
		return proncm;
	}

	public void setProncm(String proncm) {
		this.proncm = proncm;
	}

	public String getProcest() {
		return procest;
	}

	public void setProcest(String procest) {
		this.procest = procest;
	}

	public float getProprcvdavar() {
		return proprcvdavar;
	}

	public void setProprcvdavar(float proprcvdavar) {
		this.proprcvdavar = proprcvdavar;
	}

	public float getEstatu() {
		return estatu;
	}

	public void setEstatu(float estatu) {
		this.estatu = estatu;
	}

}
