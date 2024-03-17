package ma.enset.hospital.web;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Value;
import ma.enset.hospital.entities.Patient;
import ma.enset.hospital.repositories.PatientRepository;
import org.apache.el.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@AllArgsConstructor
public class PatientController {
    @Autowired
    private PatientRepository patientRepository;
    @GetMapping("/user/index")

    public String index(Model model, @RequestParam(name ="page",defaultValue = "0") int page,
                                     @RequestParam(name ="size",defaultValue = "5") int size,
                                     @RequestParam(name ="keyword",defaultValue = "") String kw){
        //Page<Patient> pagePatients = patientRepository.findAll(PageRequest.of(page,size));
        Page<Patient> pagePatients = patientRepository.findByNomContains(kw,PageRequest.of(page,size));
        model.addAttribute("listPatients",pagePatients.getContent());
        model.addAttribute("pages",new int[pagePatients.getTotalPages()]);
        model.addAttribute("currentPage",page);
        model.addAttribute("keyword",kw);
        return "patients";

    }
    @GetMapping("/admin/delete")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String delete(Long id,String keyword,int page){
        patientRepository.deleteById(id);
        // on va faire la redirection pour afficher
        // une fois je supprimer , je veux redireger  vers /index , et /index , il va afficher les nves patients
        return "redirect:/user/index?page="+page+"&keyword="+keyword;
    }
    @PostMapping("/addPatient")
    public String addPatient(@RequestParam String nom,
                             @RequestParam String dateNaissance,
                             @RequestParam(defaultValue = "false") boolean malade,
                             @RequestParam int score) {

        // Convert the dateNaissance String to a java.util.Date object
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date parsedDate;
        try {
            parsedDate = dateFormat.parse(dateNaissance);
        } catch (java.text.ParseException e) {
            // Handle the parse exception, for example, by logging it
            e.printStackTrace();
            // You might want to return an error view or redirect to the form
            return "redirect:/index";
        }

        Patient newPatient = new Patient();
        newPatient.setNom(nom);
        newPatient.setDateNaissance(parsedDate);
        newPatient.setMalade(malade);
        newPatient.setScore(score);

        patientRepository.save(newPatient);

        return "redirect:/index";
    }
    @GetMapping("/")
    public String home(){
        return "redirect:/user/index";
    }

    @GetMapping("/admin/formPatients")
    @PreAuthorize("hasRole('ROLE_ADMIN')")

    public String formPatient(Model model){
        model.addAttribute("patient", new Patient() );
        return "formPatients";
    }
    @PostMapping("/admin/savePatient")
    @PreAuthorize("hasRole('ROLE_ADMIN')")

    public String savePatient(@Valid Patient patient, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "formPatients";
        }
        patientRepository.save(patient);
        return "redirect:/user/index?keyword="+patient.getNom();
    }
    @GetMapping("/admin/editPatient")
    @PreAuthorize("hasRole('ROLE_ADMIN')")

    public String editPatient(Model model,@RequestParam(name = "id")  Long id){
        // on va récuperer le patient de BD ,
        Patient patient = patientRepository.findById(id).get();
        // si je le trouve , je l'ajoute au model
        model.addAttribute("patient",patient);
        return "editPatient";
    }



}
