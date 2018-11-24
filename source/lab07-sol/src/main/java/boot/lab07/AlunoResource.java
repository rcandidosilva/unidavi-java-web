package boot.lab07;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

public class AlunoResource extends Resource<Aluno> {
	
	public AlunoResource(Aluno aluno, Link... links) {
		super(aluno, links);
	}
	
}