package iatools;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 *
 * @author Marlos Oliveira Classe para conectar o banco de dados firebird
 */
public class DBFirebird {
	// VariÃ¡ves do e conexao com o banco firebird

	private String driver = null;
	/*
	 * esse banco é o oficial porque nao pode mudar os codigos dos produtos = banco
	 * destino
	 * 
	 * o objetivo é: Adicionar o que falta (ncm, cest, estoque, preços, tabela A,
	 * aliqueta, pis, cofins)
	 * 
	 * Adiconar os produtos que não existem no banco fiscal e que só existem no
	 * banco não fiscal
	 * 
	 * 
	 */

	private String url_syspdv_destino = "jdbc:firebirdsql:127.0.0.1/3050:C:/Users/55889/Downloads/J Cleanto/J Cleanto Fiscal/syspdv_srv.fdb";
	// banco destino é o oficial, o destino dos dados de origem nao fiscal

	private String url_syspdv_base = "jdbc:firebirdsql:127.0.0.1/3050:C:/Users/55889/Downloads/J Cleanto/J Cleanto Não Fiscal/syspdv_srv.fdb";
	// banco de origem dos dados, que vao para o banco destino (ncm, cest, estoque,
	// preços, tabela A, aliqueta, pis, cofins)

	private Properties prop = new Properties();
	// todas as consultas são na mesma query, seja no banco base ou no banco
	// destino.
	private String query = "select p.procod, p.prodes, p.seccod, p.grpcod, p.sgrcod, p.proprccst, p.proprcvdavar, p.proprcvda2, p.proprcvda3, p.proprc1, p.proncm, p.procest, p.protaba, e.estatu, pa.procodaux from produto as p left join estoque as e on e.procod = p.procod left join produtoaux as pa on pa.procod = p.procod where p.proforlin = 'N' and p.proend is null order by p.procod";

	public ArrayList<Produto> getProdutosBase() {
		driver = "org.firebirdsql.jdbc.FBDriver";

		ResultSet rs_base = null;

		Connection conn_syspdv_base = null;

		Statement stmt_syspdv_base = null;

		ArrayList<Produto> listaProdutoBase = new ArrayList<Produto>();

		prop.setProperty("user", "SYSDBA");
		prop.setProperty("password", "masterkey");
		prop.setProperty("encoding", "utf8");

		try {

			conn_syspdv_base = DriverManager.getConnection(url_syspdv_base, prop);
			stmt_syspdv_base = conn_syspdv_base.createStatement();

			rs_base = stmt_syspdv_base.executeQuery(query);

			while (rs_base.next()) {
				// System.out.println(rs_base.getString(1));
				Produto p = new Produto(rs_base.getString("procod"), rs_base.getString("prodes"),
						rs_base.getString("seccod"), rs_base.getString("grpcod"), rs_base.getString("sgrcod"),
						rs_base.getFloat("proprccst"), rs_base.getFloat("proprcvdavar"), rs_base.getFloat("proprcvda2"),
						rs_base.getFloat("proprcvda3"), rs_base.getFloat("proprc1"), rs_base.getString("proncm"),
						rs_base.getString("procest"), rs_base.getString("protaba"), rs_base.getFloat("estatu"),
						rs_base.getString("procodaux"));
				listaProdutoBase.add(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listaProdutoBase;

	}

	public void putMovimentoDestino(String procod, double movqtd, String movdoc) {
		/*
		 * Data: 27/08/2021
		 * 
		 * qual é o banco que vai inserir a movimentacao tem que ser o banco atual
		 * porque é novo que vai ser gravado o saldo do estoque
		 * 
		 */

		String driver = "org.firebirdsql.jdbc.FBDriver";
		Properties prop = new Properties();
		Connection conn = null;
		Statement stmt = null;
		prop.setProperty("user", "SYSDBA");
		prop.setProperty("password", "masterkey");
		prop.setProperty("encoding", "utf8");
		Calendar c = Calendar.getInstance();
		String data = c.get(Calendar.DAY_OF_MONTH) + "." + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR);

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url_syspdv_destino, prop);
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			while (procod.length() < 14) {
				procod = "0" + procod;
			}

			System.out.println("INSERT INTO ESTOQUE_MOVIMENTACAO"
					+ " (LOCCOD, PROCOD, MOVDAT, MOVQTD, MOVDOC, MOVMTV, MOVTIP, MOVESP, FATCOD, FUNCOD )"
					+ "VALUES ('01', '" + procod + "', '" + data + "', " + movqtd + ", 'AJUSTE MANUAL', '" + movdoc
					+ "', 'A', 'AJU', '016', '000001')");
			stmt.executeUpdate("INSERT INTO ESTOQUE_MOVIMENTACAO"
					+ " (LOCCOD, PROCOD, MOVDAT, MOVQTD, MOVDOC, MOVMTV, MOVTIP, MOVESP, FATCOD, FUNCOD )"
					+ "VALUES ('01', '" + procod + "', '" + data + "', " + movqtd + ", 'AJUSTE MANUAL', '" + movdoc
					+ "', 'A', 'AJU', '016', '000001')");
			conn.commit();
			stmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void putMovimentoDestino(HashMap<String, Double> map) {
		/*
		 * Data: 09/09/2021
		 * 
		 * qual é o banco que vai inserir a movimentacao tem que ser o banco atual
		 * porque é novo que vai ser gravado o saldo do estoque
		 * 
		 */

		String driver = "org.firebirdsql.jdbc.FBDriver";
		Properties prop = new Properties();
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		prop.setProperty("user", "SYSDBA");
		prop.setProperty("password", "masterkey");
		prop.setProperty("encoding", "utf8");
		Calendar c = Calendar.getInstance();
		String data = c.get(Calendar.DAY_OF_MONTH) + "." + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR);

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url_syspdv_destino, prop);
			pstmt = conn.prepareStatement("INSERT INTO ESTOQUE_MOVIMENTACAO"
					+ " (LOCCOD, PROCOD, MOVDAT, MOVQTD, MOVDOC, MOVMTV, MOVTIP, MOVESP, FUNCOD)"
					+ " VALUES ('01', ?, ?, ?, 'ATUALIZADO VER 11', 'IATOOLS', 'A', 'AJU', '000001')");
			for (String key : map.keySet()) {
				pstmt.setDouble(3, map.get(key));
				while (key.length() < 14) {
					key = "0" + key;
				}
				pstmt.setString(1, key);
				pstmt.setString(2, data);
				pstmt.execute();
			}
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void putEstoqueDestino(HashMap<String, Float> map) {
		/*
		 * Data: 25/08/2021
		 * 
		 * hashmap contendo o estoque de todos os produtos do banco origem que vai
		 * entrar entradar no banco destino
		 * 
		 */

		String driver = "org.firebirdsql.jdbc.FBDriver";
		Properties prop = new Properties();
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		prop.setProperty("user", "SYSDBA");
		prop.setProperty("password", "masterkey");
		prop.setProperty("encoding", "utf8");

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url_syspdv_destino, prop);
			pstmt = conn.prepareStatement("update estoque set estatu = ? where procod = ?");

			for (String key : map.keySet()) {
				pstmt.setFloat(1, map.get(key));
				pstmt.setString(2, key);
				pstmt.execute();
			}
			pstmt.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public double getSaldoEstoqueMovimentoDestino(String procod) {
		/*
		 * data: 27/08/2021
		 */
		String driver = "org.firebirdsql.jdbc.FBDriver";
		Properties prop = new Properties();

		Connection conn_atual = null;
		Statement stmt_atual = null;
		ResultSet rs_atual = null;
		prop.setProperty("user", "SYSDBA");
		prop.setProperty("password", "masterkey");
		prop.setProperty("encoding", "utf8");
		double saldo = 0;

		try {
			Class.forName(driver);
			conn_atual = DriverManager.getConnection(url_syspdv_destino, prop);
			stmt_atual = conn_atual.createStatement();

			rs_atual = stmt_atual
					.executeQuery("select sum(movqtd) as movqtd from estoque_movimentacao where procod = " + procod);
			while (rs_atual.next()) {
				saldo = rs_atual.getFloat(1);
			}

			rs_atual.close();
			stmt_atual.close();
			conn_atual.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return saldo;
	}

	public HashMap<String, Double> getSaldoEstoqueMovimentoDestino() {
		/*
		 * data: 09/09/2021
		 */
		String driver = "org.firebirdsql.jdbc.FBDriver";
		Properties prop = new Properties();

		Connection conn_destino = null;
		Statement stmt_destino = null;
		ResultSet rs_destino = null;
		prop.setProperty("user", "SYSDBA");
		prop.setProperty("password", "masterkey");
		prop.setProperty("encoding", "utf8");
		HashMap<String, Double> map = new HashMap<String, Double>();

		try {
			Class.forName(driver);
			conn_destino = DriverManager.getConnection(url_syspdv_destino, prop);
			stmt_destino = conn_destino.createStatement();

			rs_destino = stmt_destino.executeQuery(
					"select procod, sum(movqtd) from estoque_movimentacao group by procod order by procod");
			while (rs_destino.next()) {
				map.put(rs_destino.getString(1), rs_destino.getDouble(2));
			}

			rs_destino.close();
			stmt_destino.close();
			conn_destino.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return map;
	}

	public void preparaBancos() {
		driver = "org.firebirdsql.jdbc.FBDriver";
		Connection conn_syspdv_destino = null;
		Connection conn_syspdv_base = null;
		Statement stmt_syspdv_destino = null;
		Statement stmt_syspdv_base = null;

		prop.setProperty("user", "SYSDBA");
		prop.setProperty("password", "masterkey");
		prop.setProperty("encoding", "utf8");

		try {
			Class.forName(driver);

			conn_syspdv_destino = DriverManager.getConnection(url_syspdv_destino, prop);
			stmt_syspdv_destino = conn_syspdv_destino.createStatement();
			conn_syspdv_base = DriverManager.getConnection(url_syspdv_base, prop);
			stmt_syspdv_base = conn_syspdv_base.createStatement();
			conn_syspdv_base.setAutoCommit(false);
			conn_syspdv_destino.setAutoCommit(false);

			stmt_syspdv_destino.executeUpdate("update produto set proend = null");
			stmt_syspdv_base.executeUpdate("update produto set proend = null");

			stmt_syspdv_destino.executeUpdate("delete from estoque_movimentacao where movdoc = 'ATUALIZADO VER 11'");

			stmt_syspdv_base.executeUpdate("update produto set proforlin = 'S' where prodes = 'OBSOLETO'");
			stmt_syspdv_destino.executeUpdate("update produto set proforlin = 'S' where prodes = 'OBSOLETO'");

			conn_syspdv_base.commit();
			conn_syspdv_destino.commit();
			stmt_syspdv_destino.close();
			conn_syspdv_destino.close();
			stmt_syspdv_base.close();
			conn_syspdv_base.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void importaTabela(String tabela) {
		// editado 26/09/2021
		System.out.println("Importando tabela " + tabela);
		driver = "org.firebirdsql.jdbc.FBDriver";
		Connection conn_syspdv_destino = null;
		Connection conn_syspdv_base = null;
		Statement stmt_syspdv_destino = null;
		Statement stmt_syspdv_base = null;
		PreparedStatement pstmt = null;
		ResultSet rs_base = null;
		ResultSet rs_destino = null;
		prop.setProperty("user", "SYSDBA");
		prop.setProperty("password", "masterkey");
		prop.setProperty("encoding", "utf8");

		try {
			Class.forName(driver);

			conn_syspdv_destino = DriverManager.getConnection(url_syspdv_destino, prop);
			stmt_syspdv_destino = conn_syspdv_destino.createStatement();
			conn_syspdv_base = DriverManager.getConnection(url_syspdv_base, prop);
			stmt_syspdv_base = conn_syspdv_base.createStatement();
			stmt_syspdv_destino.executeUpdate("delete from " + tabela);
			rs_base = stmt_syspdv_base.executeQuery("Select * from " + tabela);
			rs_destino = stmt_syspdv_destino.executeQuery("Select * from " + tabela);

			ResultSetMetaData rsmd_base = rs_base.getMetaData();
			ResultSetMetaData rsmd_destino = rs_destino.getMetaData();
			int numColTabelaBase = rsmd_base.getColumnCount();
			
			int numColTabelaDestino = rsmd_destino.getColumnCount();
			

			String s = "insert into " + tabela + " values(";
			// montando uma string de insersai
			for (int i = 1; i <= numColTabelaDestino; i++) {
				s = s + "?";
				if (i != numColTabelaDestino) {
					s = s + ", ";
				}
			}
			s = s + ")";

			pstmt = conn_syspdv_destino.prepareStatement(s);

			while (rs_base.next()) {
				for (int i = 1; i <= numColTabelaDestino; i++) {
					if (numColTabelaBase < numColTabelaDestino) {
						if (i < numColTabelaBase) {
							pstmt.setObject(i, rs_base.getObject(i));
						} else {
							pstmt.setObject(i, null);
						}
					} else {
						pstmt.setObject(i, rs_base.getObject(i));
					}
				}
				pstmt.execute();
			}
			rs_destino.close();
			stmt_syspdv_destino.close();
			conn_syspdv_destino.close();
			rs_base.close();
			stmt_syspdv_base.close();
			conn_syspdv_base.close();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void importaProdutosCodigoAuxiliarIgual() {
		// editado 26/09/2021
		driver = "org.firebirdsql.jdbc.FBDriver";
		ResultSet rs_destino = null;
		ResultSet rs_base = null;
		Connection conn_syspdv_destino = null;
		Connection conn_syspdv_base = null;
		Statement stmt_syspdv_destino = null;
		Statement stmt_syspdv_base = null;
		ArrayList<Produto> destino = new ArrayList<Produto>();
		ArrayList<Produto> base = new ArrayList<Produto>();
		ArrayList<String> produtosAssociados = new ArrayList<String>();

		prop.setProperty("user", "SYSDBA");
		prop.setProperty("password", "masterkey");
		prop.setProperty("encoding", "utf8");

		try {
			Class.forName(driver);

			conn_syspdv_destino = DriverManager.getConnection(url_syspdv_destino, prop);
			stmt_syspdv_destino = conn_syspdv_destino.createStatement();
			conn_syspdv_base = DriverManager.getConnection(url_syspdv_base, prop);
			stmt_syspdv_base = conn_syspdv_base.createStatement();
			conn_syspdv_destino.setAutoCommit(false);
			conn_syspdv_base.setAutoCommit(false);

			rs_base = stmt_syspdv_base.executeQuery(query);
			while (rs_base.next()) {
				// System.out.println(rs_base.getString(1));
				String procodaux = rs_base.getString("procodaux");
				if (procodaux == null) {
					procodaux = "";
				}
				Produto p = new Produto(rs_base.getString("procod"), rs_base.getString("prodes"),
						rs_base.getString("seccod"), rs_base.getString("grpcod"), rs_base.getString("sgrcod"),
						rs_base.getFloat("proprccst"), rs_base.getFloat("proprcvdavar"), rs_base.getFloat("proprcvda2"),
						rs_base.getFloat("proprcvda3"), rs_base.getFloat("proprc1"), rs_base.getString("proncm"),
						rs_base.getString("procest"), rs_base.getString("protaba"), rs_base.getFloat("estatu"),
						rs_base.getString("procodaux"));
				base.add(p);
			}

			rs_destino = stmt_syspdv_destino.executeQuery(query);

			while (rs_destino.next()) {
				// System.out.println(rs_destino.getString(1));
				String procodaux = rs_destino.getString("procodaux");
				if (procodaux == null) {
					procodaux = "";
				}
				Produto p = new Produto(rs_destino.getString("procod"), rs_destino.getString("prodes"),
						rs_destino.getString("seccod"), rs_destino.getString("grpcod"), rs_destino.getString("sgrcod"),
						rs_destino.getFloat("proprccst"), rs_destino.getFloat("proprcvdavar"),
						rs_destino.getFloat("proprcvda2"), rs_destino.getFloat("proprcvda3"),
						rs_destino.getFloat("proprc1"), rs_destino.getString("proncm"), rs_destino.getString("procest"),
						rs_destino.getString("protaba"), rs_destino.getFloat("estatu"),
						rs_destino.getString("procodaux"));
				destino.add(p);
			}
			int numeroRegistros = 0;
			// o campo produto.procodorigem na base fiscal vai guardar o codigo do produto
			// do sistema nao fiscal

			for (Produto p_destino : destino) {
				System.out.println("numero de registros: " + ++numeroRegistros + "/" + destino.size() + "/Associados: "
						+ produtosAssociados.size());
				for (Produto p_base : base) {
					String procodaux_base = p_base.getProcodaux();
					String procodaux_destino = p_destino.getProcodaux();
					if (procodaux_base != null && procodaux_destino != null) {
						procodaux_base = procodaux_base.trim();
						procodaux_destino = procodaux_destino.trim();
						if (procodaux_destino.equals(procodaux_base) && !procodaux_destino.equals("")
								&& !produtosAssociados.contains(p_destino.getProcod())) {
							System.out.println(p_destino.getProcodaux() + "|" + p_base.getProcodaux());
							// atualizar preço de custo, preço de venda e estoque

							System.out.println("update produto set proend = '" + p_base.getProcod() + "', proprccst = '"
									+ p_base.getProprccst() + "', proprcvdavar = " + p_base.getProprcvdavar()
									+ ", proprcvda2 = " + p_base.getProprcvda2() + ", proprcvda3 = "
									+ p_base.getProprcvda3() + ", seccod = '" + p_base.getSeccod() + "', grpcod = '"
									+ p_base.getGrpcod() + "', sgrcod = '" + p_base.getSgrcod() + "' where procod = '"
									+ p_destino.getProcod() + "'");
							stmt_syspdv_destino.executeUpdate("update produto set proend = '" + p_base.getProcod()
									+ "', proprccst = '" + p_base.getProprccst() + "', proprcvdavar = "
									+ p_base.getProprcvdavar() + ", proprcvda2 = " + p_base.getProprcvda2()
									+ ", proprcvda3 = " + p_base.getProprcvda3() + ", seccod = '" + p_base.getSeccod()
									+ "', grpcod = '" + p_base.getGrpcod() + "', sgrcod = '" + p_base.getSgrcod()
									+ "' where procod = '" + p_destino.getProcod() + "'");

							// fazer ajuste de movimentacao
							// quanto tem na movimentacao do destino?
							// qual é o estoque que deve contar no estatu?
							// lembrar que a unica coisa que liga os dois produtos é o codigo auxliar

							double saldoMovimentacaoDestino = this
									.getSaldoEstoqueMovimentoDestino(p_destino.getProcod());
							double saldoMovimentacaoDestinoCorreto = p_base.getEstatu() - saldoMovimentacaoDestino;

							Calendar c = Calendar.getInstance();
							String data = c.get(Calendar.DAY_OF_MONTH) + "." + (c.get(Calendar.MONTH) + 1) + "."
									+ c.get(Calendar.YEAR);
							System.out.println("INSERT INTO ESTOQUE_MOVIMENTACAO"
									+ " (LOCCOD, PROCOD, MOVDAT, MOVQTD, MOVDOC, MOVMTV, MOVTIP, MOVESP, FATCOD, FUNCOD )"
									+ "VALUES ('01', '" + p_destino.getProcod() + "', '" + data + "', "
									+ saldoMovimentacaoDestinoCorreto + ", 'AJUSTE MANUAL', '" + "importado ver 11"
									+ "', 'A', 'AJU', '016', '000001')");
							stmt_syspdv_destino.executeUpdate("INSERT INTO ESTOQUE_MOVIMENTACAO"
									+ " (LOCCOD, PROCOD, MOVDAT, MOVQTD, MOVDOC, MOVMTV, MOVTIP, MOVESP, FATCOD, FUNCOD )"
									+ "VALUES ('01', '" + p_destino.getProcod() + "', '" + data + "', "
									+ saldoMovimentacaoDestinoCorreto + ", 'AJUSTE MANUAL', '" + "importado ver 11"
									+ "', 'A', 'AJU', '016', '000001')");

							System.out.println("update estoque set estatu = " + p_base.getEstatu() + " where procod = '"
									+ p_destino.getProcod() + "'");
							stmt_syspdv_destino.executeUpdate("update estoque set estatu = " + p_base.getEstatu()
									+ " where procod = '" + p_destino.getProcod() + "'");

							System.out.println("update produto set proend = '" + p_destino.getProcod()
									+ "' where procod = " + p_base.getProcod());
							stmt_syspdv_base.executeUpdate("update produto set proend = '" + p_destino.getProcod()
									+ "' where procod = " + p_base.getProcod());

							produtosAssociados.add(p_destino.getProcod());
							System.out.println("comitando base");
							conn_syspdv_base.commit();
							System.out.println("comitando destino");
							conn_syspdv_destino.commit();
						}
					}
				}
			}

			rs_destino.close();
			rs_base.close();
			stmt_syspdv_destino.close();
			conn_syspdv_destino.close();
			stmt_syspdv_base.close();
			conn_syspdv_base.close();
		}

		catch (Exception e) {

			try {
				conn_syspdv_base.rollback();
				conn_syspdv_destino.rollback();
				System.out.println("Parando o firebird");
				Runtime.getRuntime().exec("net stop \"FireBird Server - DefaultInstance\"");
				System.out.println("Iniciando o firebird");
				Runtime.getRuntime().exec("net start \"FireBird Server - DefaultInstance\"");
				System.out.println("reiniciando o programa");
				Runtime.getRuntime().exec("java -jar C:\\eclipse_sistemas\\IALucroReal\\dist\\IATools.java");

			} catch (Exception e1) {
				e1.printStackTrace();
			}

			e.printStackTrace();

		}
	}

	public void importaProdutosDescricaoSimilar(int similaridade) {
		driver = "org.firebirdsql.jdbc.FBDriver";
		ResultSet rs_destino = null;
		ResultSet rs_base = null;
		Connection conn_syspdv_destino = null;
		Connection conn_syspdv_base = null;
		Statement stmt_syspdv_destino = null;
		Statement stmt_syspdv_base = null;
		ArrayList<Produto> destino = new ArrayList<Produto>();
		ArrayList<Produto> base = new ArrayList<Produto>();
		ArrayList<String> produtosAssociados = new ArrayList<String>();
		prop.setProperty("user", "SYSDBA");
		prop.setProperty("password", "masterkey");
		prop.setProperty("encoding", "utf8");
		IAString iastring = new IAString();
		try {
			conn_syspdv_destino = DriverManager.getConnection(url_syspdv_destino, prop);
			stmt_syspdv_destino = conn_syspdv_destino.createStatement();
			conn_syspdv_base = DriverManager.getConnection(url_syspdv_base, prop);
			stmt_syspdv_base = conn_syspdv_base.createStatement();
			conn_syspdv_destino.setAutoCommit(false);
			conn_syspdv_base.setAutoCommit(false);
			rs_base = stmt_syspdv_base.executeQuery(query);
			base = new ArrayList<Produto>();
			while (rs_base.next()) {

				Produto p = new Produto(rs_base.getString("procod"), rs_base.getString("prodes"),
						rs_base.getString("seccod"), rs_base.getString("grpcod"), rs_base.getString("sgrcod"),
						rs_base.getFloat("proprccst"), rs_base.getFloat("proprcvdavar"), rs_base.getFloat("proprcvda2"),
						rs_base.getFloat("proprcvda3"), rs_base.getFloat("proprc1"), rs_base.getString("proncm"),
						rs_base.getString("procest"), rs_base.getString("protaba"), rs_base.getFloat("estatu"),
						rs_base.getString("procodaux"));
				base.add(p);
			}
			System.out.println("Numero de produtos na base: " + base.size());

			destino = new ArrayList<Produto>();
			rs_destino = stmt_syspdv_destino.executeQuery(query);

			while (rs_destino.next()) {

				Produto p = new Produto(rs_destino.getString("procod"), rs_destino.getString("prodes"),
						rs_destino.getString("seccod"), rs_destino.getString("grpcod"), rs_destino.getString("sgrcod"),
						rs_destino.getFloat("proprccst"), rs_destino.getFloat("proprcvdavar"),
						rs_destino.getFloat("proprcvda2"), rs_destino.getFloat("proprcvda3"),
						rs_destino.getFloat("proprc1"), rs_destino.getString("proncm"), rs_destino.getString("procest"),
						rs_destino.getString("protaba"), rs_destino.getFloat("estatu"),
						rs_destino.getString("procodaux"));
				destino.add(p);
			}
			int numeroRegistros = 0;
			for (Produto p_destino : destino) {
				System.out.println("numero de registros: " + ++numeroRegistros + "/" + destino.size() + "/Associados: "
						+ produtosAssociados.size() + "/" + similaridade + "%");
				for (Produto p_base : base) {
					float similar = iastring.similar4(p_destino.getProdes(), p_base.getProdes());
					if (similar == similaridade && !produtosAssociados.contains(p_destino.getProcod())) {
						System.out.println("DESTINO: " + p_destino.getProdes());
						System.out.println("   BASE: " + p_base.getProdes());
						System.out.println("similar em " + similar + "%");
						System.out.println("-------------------------------------");

						stmt_syspdv_destino.executeUpdate("update produto set proend = '" + p_base.getProcod()
								+ "', proprccst = '" + p_base.getProprccst() + "', proprcvdavar = "
								+ p_base.getProprcvdavar() + ",proprcvda2 = " + p_base.getProprcvda2()
								+ ", proprcvda3 = " + p_base.getProprcvda3() + ", seccod = '" + p_base.getSeccod()
								+ "', grpcod = '" + p_base.getGrpcod() + "', sgrcod = '" + p_base.getSgrcod()
								+ "' where procod = '" + p_destino.getProcod() + "'");
						System.out.println("update produto set proend = '" + p_base.getProcod() + "', proprccst = '"
								+ p_base.getProprccst() + "', proprcvdavar = " + p_base.getProprcvdavar()
								+ ",proprcvda2 = " + p_base.getProprcvda2() + ", proprcvda3 = " + p_base.getProprcvda3()
								+ ", seccod = '" + p_base.getSeccod() + "', grpcod = '" + p_base.getGrpcod()
								+ "', sgrcod = '" + p_base.getSgrcod() + "' where procod = '" + p_destino.getProcod()
								+ "'");
						// fazer ajuste de movimentacao
						// quanto tem na movimentacao do destino?
						// qual é o estoque que deve contar no estatu?
						// lembrar que a unica coisa que liga os dois produtos é o codigo auxliar

						double saldoMovimentacaoDestino = this.getSaldoEstoqueMovimentoDestino(p_destino.getProcod());
						double saldoMovimentacaoDestinoCorreto = p_base.getEstatu() - saldoMovimentacaoDestino;

						Calendar c = Calendar.getInstance();
						String data = c.get(Calendar.DAY_OF_MONTH) + "." + (c.get(Calendar.MONTH) + 1) + "."
								+ c.get(Calendar.YEAR);
						System.out.println("INSERT INTO ESTOQUE_MOVIMENTACAO"
								+ " (LOCCOD, PROCOD, MOVDAT, MOVQTD, MOVDOC, MOVMTV, MOVTIP, MOVESP, FATCOD, FUNCOD )"
								+ "VALUES ('01', '" + p_destino.getProcod() + "', '" + data + "', "
								+ saldoMovimentacaoDestinoCorreto + ", 'AJUSTE MANUAL', '" + "importado ver 11"
								+ "', 'A', 'AJU', '016', '000001')");
						stmt_syspdv_destino.executeUpdate("INSERT INTO ESTOQUE_MOVIMENTACAO"
								+ " (LOCCOD, PROCOD, MOVDAT, MOVQTD, MOVDOC, MOVMTV, MOVTIP, MOVESP, FATCOD, FUNCOD )"
								+ "VALUES ('01', '" + p_destino.getProcod() + "', '" + data + "', "
								+ saldoMovimentacaoDestinoCorreto + ", 'AJUSTE MANUAL', '" + "importado ver 11"
								+ "', 'A', 'AJU', '016', '000001')");

						System.out.println("update estoque set estatu = " + p_base.getEstatu() + " where procod = '"
								+ p_destino.getProcod() + "'");
						stmt_syspdv_destino.executeUpdate("update estoque set estatu = " + p_base.getEstatu()
								+ " where procod = '" + p_destino.getProcod() + "'");

						System.out.println("update produto set proend = '" + p_destino.getProcod()
								+ "' where procod = " + p_base.getProcod());
						stmt_syspdv_base.executeUpdate("update produto set proend = '" + p_destino.getProcod()
								+ "' where procod = " + p_base.getProcod());

						produtosAssociados.add(p_destino.getProcod());
						System.out.println("comitando base");
						conn_syspdv_base.commit();
						System.out.println("comitando destino");
						conn_syspdv_destino.commit();
					}
				}
			}

			rs_destino.close();
			rs_base.close();
			stmt_syspdv_destino.close();
			conn_syspdv_destino.close();
			stmt_syspdv_base.close();
			conn_syspdv_base.close();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	public void importaProdutosDescricaoSimilarCod(int similaridade) {
		driver = "org.firebirdsql.jdbc.FBDriver";
		ResultSet rs_destino = null;
		ResultSet rs_base = null;
		Connection conn_syspdv_destino = null;
		Connection conn_syspdv_base = null;
		Statement stmt_syspdv_destino = null;
		Statement stmt_syspdv_base = null;
		ArrayList<Produto> destino = new ArrayList<Produto>();
		ArrayList<Produto> base = new ArrayList<Produto>();
		ArrayList<String> produtosAssociados = new ArrayList<String>();
		prop.setProperty("user", "SYSDBA");
		prop.setProperty("password", "masterkey");
		prop.setProperty("encoding", "utf8");
		IAString iastring = new IAString();
		try {
			conn_syspdv_destino = DriverManager.getConnection(url_syspdv_destino, prop);
			stmt_syspdv_destino = conn_syspdv_destino.createStatement();
			conn_syspdv_base = DriverManager.getConnection(url_syspdv_base, prop);
			stmt_syspdv_base = conn_syspdv_base.createStatement();
			rs_base = stmt_syspdv_base.executeQuery(query);
			base = new ArrayList<Produto>();
			while (rs_base.next()) {

				Produto p = new Produto(rs_base.getString("procod"), rs_base.getString("prodes"),
						rs_base.getString("seccod"), rs_base.getString("grpcod"), rs_base.getString("sgrcod"),
						rs_base.getFloat("proprccst"), rs_base.getFloat("proprcvdavar"), rs_base.getFloat("proprcvda2"),
						rs_base.getFloat("proprcvda3"), rs_base.getFloat("proprc1"), rs_base.getString("proncm"),
						rs_base.getString("procest"), rs_base.getString("protaba"), rs_base.getFloat("estatu"),
						rs_base.getString("procodaux"));
				base.add(p);
			}
			System.out.println("Numero de produtos na base: " + base.size());

			destino = new ArrayList<Produto>();
			rs_destino = stmt_syspdv_destino.executeQuery(query);

			while (rs_destino.next()) {

				Produto p = new Produto(rs_destino.getString("procod"), rs_destino.getString("prodes"),
						rs_destino.getString("seccod"), rs_destino.getString("grpcod"), rs_destino.getString("sgrcod"),
						rs_destino.getFloat("proprccst"), rs_destino.getFloat("proprcvdavar"),
						rs_destino.getFloat("proprcvda2"), rs_destino.getFloat("proprcvda3"),
						rs_destino.getFloat("proprc1"), rs_destino.getString("proncm"), rs_destino.getString("procest"),
						rs_destino.getString("protaba"), rs_destino.getFloat("estatu"),
						rs_destino.getString("procodaux"));
				destino.add(p);
			}
			int numeroRegistros = 0;
			for (Produto p_destino : destino) {
				System.out.println("numero de registros: " + ++numeroRegistros + "/" + destino.size() + "/Associados: "
						+ produtosAssociados.size() + "/" + similaridade + "%");
				for (Produto p_base : base) {
					float similar = iastring.similar4(p_destino.getProdes(), p_base.getProdes());
					if (similar >= similaridade && !produtosAssociados.contains(p_destino.getProcod())
							&& p_destino.getProcod().equals(p_base.getProcod())
							&& p_destino.getProcod().equals(p_base.getProcod())) {
						System.out.println("DESTINO: " + p_destino.getProdes());
						System.out.println("   BASE: " + p_base.getProdes());
						System.out.println("similar em " + similar + "%");
						System.out.println("-------------------------------------");

						stmt_syspdv_destino.executeUpdate("update produto set proend = '" + p_base.getProcod()
								+ "', proprccst = '" + p_base.getProprccst() + "', proprcvdavar = "
								+ p_base.getProprcvdavar() + ",proprcvda2 = " + p_base.getProprcvda2()
								+ ", proprcvda3 = " + p_base.getProprcvda3() + ", seccod = '" + p_base.getSeccod()
								+ "', grpcod = '" + p_base.getGrpcod() + "', sgrcod = '" + p_base.getSgrcod()
								+ "' where procod = '" + p_destino.getProcod() + "'");
						System.out.println("update produto set proend = '" + p_base.getProcod() + "', proprccst = '"
								+ p_base.getProprccst() + "', proprcvdavar = " + p_base.getProprcvdavar()
								+ ",proprcvda2 = " + p_base.getProprcvda2() + ", proprcvda3 = " + p_base.getProprcvda3()
								+ ", seccod = '" + p_base.getSeccod() + "', grpcod = '" + p_base.getGrpcod()
								+ "', sgrcod = '" + p_base.getSgrcod() + "' where procod = '" + p_destino.getProcod()
								+ "'");
						// fazer ajuste de movimentacao
						// quanto tem na movimentacao do destino?
						// qual é o estoque que deve contar no estatu?
						// lembrar que a unica coisa que liga os dois produtos é o codigo auxliar

						double saldoMovimentacaoDestino = this.getSaldoEstoqueMovimentoDestino(p_destino.getProcod());
						double saldoMovimentacaoDestinoCorreto = p_base.getEstatu() - saldoMovimentacaoDestino;

						this.putMovimentoDestino(p_destino.getProcod(), saldoMovimentacaoDestinoCorreto,
								"importado ver 11");

						stmt_syspdv_destino.executeUpdate("update estoque set estatu = " + p_base.getEstatu()
								+ " where procod = '" + p_destino.getProcod() + "'");
						System.out.println("update estoque set estatu = " + p_base.getEstatu() + " where procod = '"
								+ p_destino.getProcod() + "'");
						stmt_syspdv_base.executeUpdate("update produto set proend = '" + p_destino.getProcod()
								+ "' where procod = " + p_base.getProcod());
						produtosAssociados.add(p_destino.getProcod());
					}
				}
			}

			rs_destino.close();
			rs_base.close();
			stmt_syspdv_destino.close();
			conn_syspdv_destino.close();
			stmt_syspdv_base.close();
			conn_syspdv_base.close();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}
	public void importaLevenshtein(int similaridade) {
		driver = "org.firebirdsql.jdbc.FBDriver";
		ResultSet rs_destino = null;
		ResultSet rs_base = null;
		Connection conn_syspdv_destino = null;
		Connection conn_syspdv_base = null;
		Statement stmt_syspdv_destino = null;
		Statement stmt_syspdv_base = null;
		ArrayList<Produto> destino = new ArrayList<Produto>();
		ArrayList<Produto> base = new ArrayList<Produto>();
		ArrayList<String> produtosAssociados = new ArrayList<String>();
		prop.setProperty("user", "SYSDBA");
		prop.setProperty("password", "masterkey");
		prop.setProperty("encoding", "utf8");
		IAString iastring = new IAString();
		Levenshtein l = new Levenshtein();
		try {
			conn_syspdv_destino = DriverManager.getConnection(url_syspdv_destino, prop);
			stmt_syspdv_destino = conn_syspdv_destino.createStatement();
			conn_syspdv_base = DriverManager.getConnection(url_syspdv_base, prop);
			stmt_syspdv_base = conn_syspdv_base.createStatement();
			rs_base = stmt_syspdv_base.executeQuery(query);
			base = new ArrayList<Produto>();
			while (rs_base.next()) {

				Produto p = new Produto(rs_base.getString("procod"), rs_base.getString("prodes"),
						rs_base.getString("seccod"), rs_base.getString("grpcod"), rs_base.getString("sgrcod"),
						rs_base.getFloat("proprccst"), rs_base.getFloat("proprcvdavar"), rs_base.getFloat("proprcvda2"),
						rs_base.getFloat("proprcvda3"), rs_base.getFloat("proprc1"), rs_base.getString("proncm"),
						rs_base.getString("procest"), rs_base.getString("protaba"), rs_base.getFloat("estatu"),
						rs_base.getString("procodaux"));
				base.add(p);
			}
			System.out.println("Numero de produtos na base: " + base.size());

			destino = new ArrayList<Produto>();
			rs_destino = stmt_syspdv_destino.executeQuery(query);

			while (rs_destino.next()) {

				Produto p = new Produto(rs_destino.getString("procod"), rs_destino.getString("prodes"),
						rs_destino.getString("seccod"), rs_destino.getString("grpcod"), rs_destino.getString("sgrcod"),
						rs_destino.getFloat("proprccst"), rs_destino.getFloat("proprcvdavar"),
						rs_destino.getFloat("proprcvda2"), rs_destino.getFloat("proprcvda3"),
						rs_destino.getFloat("proprc1"), rs_destino.getString("proncm"), rs_destino.getString("procest"),
						rs_destino.getString("protaba"), rs_destino.getFloat("estatu"),
						rs_destino.getString("procodaux"));
				destino.add(p);
			}
			int numeroRegistros = 0;
			for (Produto p_destino : destino) {
				System.out.println("numero de registros: " + ++numeroRegistros + "/" + destino.size() + "/Associados: "
						+ produtosAssociados.size() + "/" + similaridade + "%");
				for (Produto p_base : base) {
					float similar = l.calculate2(p_destino.getProdes(), p_base.getProdes());
					if (similar >= similaridade && !produtosAssociados.contains(p_destino.getProcod())
							&& p_destino.getProcod().equals(p_base.getProcod())
							&& p_destino.getProcod().equals(p_base.getProcod())) {
						System.out.println("DESTINO: " + p_destino.getProdes());
						System.out.println("   BASE: " + p_base.getProdes());
						System.out.println("similar em " + similar + "%");
						System.out.println("-------------------------------------");

						stmt_syspdv_destino.executeUpdate("update produto set proend = '" + p_base.getProcod()
								+ "', proprccst = '" + p_base.getProprccst() + "', proprcvdavar = "
								+ p_base.getProprcvdavar() + ",proprcvda2 = " + p_base.getProprcvda2()
								+ ", proprcvda3 = " + p_base.getProprcvda3() + ", seccod = '" + p_base.getSeccod()
								+ "', grpcod = '" + p_base.getGrpcod() + "', sgrcod = '" + p_base.getSgrcod()
								+ "' where procod = '" + p_destino.getProcod() + "'");
						System.out.println("update produto set proend = '" + p_base.getProcod() + "', proprccst = '"
								+ p_base.getProprccst() + "', proprcvdavar = " + p_base.getProprcvdavar()
								+ ",proprcvda2 = " + p_base.getProprcvda2() + ", proprcvda3 = " + p_base.getProprcvda3()
								+ ", seccod = '" + p_base.getSeccod() + "', grpcod = '" + p_base.getGrpcod()
								+ "', sgrcod = '" + p_base.getSgrcod() + "' where procod = '" + p_destino.getProcod()
								+ "'");
						// fazer ajuste de movimentacao
						// quanto tem na movimentacao do destino?
						// qual é o estoque que deve contar no estatu?
						// lembrar que a unica coisa que liga os dois produtos é o codigo auxliar

						double saldoMovimentacaoDestino = this.getSaldoEstoqueMovimentoDestino(p_destino.getProcod());
						double saldoMovimentacaoDestinoCorreto = p_base.getEstatu() - saldoMovimentacaoDestino;

						this.putMovimentoDestino(p_destino.getProcod(), saldoMovimentacaoDestinoCorreto,
								"importado ver 11");

						stmt_syspdv_destino.executeUpdate("update estoque set estatu = " + p_base.getEstatu()
								+ " where procod = '" + p_destino.getProcod() + "'");
						System.out.println("update estoque set estatu = " + p_base.getEstatu() + " where procod = '"
								+ p_destino.getProcod() + "'");
						stmt_syspdv_base.executeUpdate("update produto set proend = '" + p_destino.getProcod()
								+ "' where procod = " + p_base.getProcod());
						produtosAssociados.add(p_destino.getProcod());
					}
				}
			}

			rs_destino.close();
			rs_base.close();
			stmt_syspdv_destino.close();
			conn_syspdv_destino.close();
			stmt_syspdv_base.close();
			conn_syspdv_base.close();

		} catch (Exception e) {

			e.printStackTrace();

		}

	}



}
