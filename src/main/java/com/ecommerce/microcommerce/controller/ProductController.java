package com.ecommerce.microcommerce.controller;

import com.ecommerce.microcommerce.dao.ProductDao;
import com.ecommerce.microcommerce.exceptions.ProduitGratuitException;
import com.ecommerce.microcommerce.exceptions.ProduitIntrouvableException;
import com.ecommerce.microcommerce.model.Product;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@Api(description = "Gestion des produits")
public class ProductController {

    @Autowired
    private ProductDao productDao;

    //Produits
    @GetMapping(value = "Produits")
    public List<Product> listProduits() {
        return productDao.findAll();
    }


    //Produits/{id}

    @ApiOperation(value = "Récupère un produit selon son ID ")
    @GetMapping(value = "Produits/{id}")

    public Product afficherUnProduit(@PathVariable int id) throws ProduitIntrouvableException {
        Product product = productDao.findById(id);

        if (product == null) throw new ProduitIntrouvableException("Le produit avec l'id " + id + " n'existe pas");

        return product;

    }


    @PostMapping(value = "/Produits")
    public ResponseEntity<Void> ajouterProduit(@Valid @RequestBody Product product) throws ProduitGratuitException {

        if(product.getPrix() == 0) throw new ProduitGratuitException("Le prix de vente est = 0");

        Product product1 = productDao.save(product);

        if (product == null) {
            return ResponseEntity.noContent().build();
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(product1.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping(value = "test/produits/{prixLimit}")
    public List<Product> testDeRequetes(@PathVariable int prixLimit) {
        return productDao.chercherUnProduitCher(prixLimit);
    }


    @GetMapping(value = "AdminProduits")

    public String calculerMargeProduit() {
        String res = "";
        List<Product> products = productDao.findAll();
        try {


            for (Product p : products) {

                res = res + " ''Product { id = " + p.getId() + ", nom = '" + p.getNom() + "', prix =" + p.getPrix() + " }: ''";
                if (p.getPrixAchat() > p.getPrix())
                    res = res + (p.getPrixAchat() - p.getPrix()) + ",\n";
                else
                    res = res + (p.getPrix() - p.getPrixAchat()) + ",\n";

            }
            return res;
        } catch (Exception e) {
            return "erreur";
        }
    }

    @GetMapping(value = "/Trier")
    public List<Product> trierProduitsParOrdreAlphabetique() {

        List<Product> p = productDao.findAll(Sort.by(Sort.Direction.ASC, "nom"));
        return p;
    }
}

