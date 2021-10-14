package iatools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IAString {
	public float similar(String s1, String s2) {
		s1 = s1.toUpperCase(); // deixa tudo maiusculo
		s2 = s2.toUpperCase();

		s1 = s1.replace("\\s+", " ");// remover os espaços duplicados (\s+ expressao regular)
		s2 = s2.replace("\\s+", " ");

		s1 = s1.trim(); // remover espaços do inicio e fim
		s2 = s2.trim();

		String palavra1[] = s1.split(" ");
		String palavra2[] = s2.split(" ");

		// melhor comparar com o List.contain, então vamos converter o array em list
		List<String> lista1 = Arrays.asList(palavra1);
		List<String> lista2 = Arrays.asList(palavra2);

		// vamos comparar palavra a palavra, dando um percentual no final baseado na s1
		// como sendo principal

		int i1 = lista1.size(); // numero de palavras em s1

		for (String s : lista1) {
			if (lista2.contains(s)) {

			} else {
				--i1;
			}
		}

		return (i1 * 100) / lista1.size(); // retornar em percentual - regra de 3
	}

	public float similar2(String s1, String s2) {
		s1 = s1.toUpperCase(); // deixa tudo maiusculo
		s2 = s2.toUpperCase();

		s1 = s1.replace("\\s+", " ");// remover os espaços duplicados (\s+ expressao regular)
		s2 = s2.replace("\\s+", " ");

		s1 = s1.trim(); // remover espaços do inicio e fim
		s2 = s2.trim();

		String palavra1[] = s1.split(" ");
		String palavra2[] = s2.split(" ");

		// melhor comparar com o List.contain, então vamos converter o array em list
		List<String> lista1 = Arrays.asList(palavra1);
		List<String> lista2 = Arrays.asList(palavra2);

		// vamos comparar palavra a palavra, dando um percentual no final baseado na s1
		// como sendo principal

		int tamanho_s1 = s1.length();
		int tamanho_s2 = s2.length();
		float media_s1_s2 = (tamanho_s1 + tamanho_s2) / 2;
		float bkp_media_s1_s2 = media_s1_s2;

		for (String s : lista1) {
			if (lista2.contains(s)) {

			} else {
				media_s1_s2 = media_s1_s2 - s.length();
			}
		}

		return (media_s1_s2 * 100) / bkp_media_s1_s2; // retornar em percentual - regra de 3
	}

	public float similar3(String s1, String s2) {
		// s1 é correto e s2 é similar
		// 100% seignifica que s2 é igual a s1

		s1 = s1.toUpperCase(); // deixa tudo maiusculo
		s2 = s2.toUpperCase();

		s1 = s1.replace("\\s+", " ");// remover os espaços duplicados (\s+ expressao regular)
		s2 = s2.replace("\\s+", " ");

		s1 = s1.trim(); // remover espaços do inicio e fim
		s2 = s2.trim();

		String palavra1[] = s1.split(" ");
		String palavra2[] = s2.split(" ");

		// melhor comparar com o List.contain, então vamos converter o array em list
		List<String> lista1 = Arrays.asList(palavra1);
		List<String> lista2 = Arrays.asList(palavra2);

		// vamos comparar palavra a palavra, dando um percentual no final baseado na s1
		// como sendo principal

		int pontos = 0;

		if (s2.equals(s1)) {
			return 100;
		} else {
			for (String s : lista2) {
				if (lista1.contains(s)) {
					pontos = s.length(); // cada palavra certa vai somando pontos até 100% = s2.lenght()
				}
				// System.out.println("palavra " + s + " - pontos: " + pontos + "/" +
				// s2.length());
			}
		}

		return (pontos * 100) / s2.length(); // retornar em percentual - regra de 3
	}

	public float similar4(String s1, String s2) {
		// s1 é correto e s2 é similar
		// 100% seignifica que s2 é igual a s1
		// compara o numero de pontos com o maior lenght, s1 ou s2

		s1 = s1.toUpperCase(); // deixa tudo maiusculo
		s2 = s2.toUpperCase();
		s1 = s1.replaceAll("\\s+", " ");// remover os espaços duplicados (\s+ expressao regular)
		s2 = s2.replaceAll("\\s+", " ");
		
		s1 = s1.trim(); // remover espaços do inicio e fim
		s2 = s2.trim();

		String palavra1[] = s1.split(" ");
		String palavra2[] = s2.split(" ");

		// melhor comparar com o List.contain, então vamos converter o array em list
		List<String> lista1 = Arrays.asList(palavra1);
		List<String> lista2 = Arrays.asList(palavra2);

		// vamos comparar palavra a palavra, dando um percentual no final baseado na s1
		// como sendo principal

		int pontos = 0;

		// define quem é maior, s1 ou s2
		int maior_string = s1.length();
		if (s2.length() > s1.length()) {
			maior_string = s2.length();
		}
		
		
		int numero_espacos_lista1 = lista1.size() -1;
		int numero_espacos_lista2 = lista2.size() -1;
		int numero_espacos = 0;
		if(numero_espacos_lista1 <= numero_espacos_lista2) {
			numero_espacos = numero_espacos_lista1;
		}
		if(numero_espacos_lista1 > numero_espacos_lista2) {
			numero_espacos = numero_espacos_lista2;
		}
		
		if (s2.equals(s1)) {
			return 100;
		} else {
			
			for (String s : lista2) {
				if (lista1.contains(s)) {
					pontos = pontos + s.length(); // cada palavra certa vai somando pontos até 100% = s2.lenght()
					//System.out.println(pontos);
				}
			}
		}
		return ((pontos+numero_espacos) * 100) / maior_string; // retornar em percentual - regra de 3
	}
}
