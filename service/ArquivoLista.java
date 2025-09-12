package service;

import model.Lista;

public class ArquivoLista extends service.Arquivo<Lista> {

    HashExtensivel<ParCodigoId> indiceIndireto;

    public ArquivoLista() throws Exception {
        super("listas", Lista.class.getConstructor());
        indiceIndireto = new HashExtensivel<>(
            ParCodigoId.class.getConstructor(), 
            4, 
            ".\\dados\\listas\\indiceLista.d.db",   // diretório
            ".\\dados\\listas\\indiceLista.c.db"    // cestos 
        );
    }

    @Override
    public int create(Lista l) throws Exception {
        int id = super.create(l);
        indiceIndireto.create(new ParCodigoId(l.getNome(), id));
        return id;
    }

    public Lista read(String email) throws Exception {
    	ParCodigoId pei = indiceIndireto.read(ParCodigoId.hash(email));
        if(pei == null)
            return null;
        return read(pei.getId());
    }
    
    public boolean delete(String email) throws Exception {
    	ParCodigoId pei = indiceIndireto.read(ParCodigoId.hash(email));
        if(pei != null) 
            if(delete(pei.getId())) 
                return indiceIndireto.delete(ParCodigoId.hash(email));
        return false;
    }

    @Override
    public boolean delete(int id) throws Exception {
        Lista l = super.read(id);
        if(l != null) {
            if(super.delete(id))
                return indiceIndireto.delete(ParCodigoId.hash(l.getNome()));
        }
        return false;
    }

    @Override
    public boolean update(Lista novoUsuario) throws Exception {
    	Lista usuarioVelho = read(novoUsuario.getNome());
        if(super.update(novoUsuario)) {
            if(novoUsuario.getNome().compareTo(usuarioVelho.getNome())!=0) {
            	indiceIndireto.delete(ParCodigoId.hash(usuarioVelho.getNome()));
            	indiceIndireto.create(new ParCodigoId(novoUsuario.getNome(), novoUsuario.getId()));
            }
            return true;
        }
        return false;
    }
}