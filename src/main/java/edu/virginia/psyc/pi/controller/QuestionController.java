package edu.virginia.psyc.pi.controller;

import edu.virginia.psyc.pi.domain.Participant;
import edu.virginia.psyc.pi.domain.Session;
import edu.virginia.psyc.pi.persistence.ParticipantDAO;
import edu.virginia.psyc.pi.persistence.ParticipantRepository;
import edu.virginia.psyc.pi.persistence.Questionnaire.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dan
 * Date: 3/26/14
 * Time: 10:04 PM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping("/questions")
public class QuestionController {

    private DASS21_ASRepository dass21_asRepository;
    private DASS21_DSRepository dass21_dsRepository;
    private QOLRepository qol_Repository;
    private AUDIT_Repository audit_Repository;
    private FollowUp_ChangeInTreatment_Repository followup_Repository;
    private ImpactAnxiousImagery_Repository impact_Repository;
    private MentalHealthHxTxRepository mh_Repository;
    private MultiUserExperienceRepository mue_Repository;
    private PilotUserExperienceRepository pue_Repository;
    private CredibilityRepository credibilityRepository;
    private DemographicRepository demographicRepository;
    private ParticipantRepository participantRepository;
    private AnxiousImageryPrime_Repository anxiousImageryPrime_Repository;
    private NeutralImageryPrime_Repository neutralImageryPrime_Repository;
    private StateAnxietyRepository stateAnxiety_Repository;
//    private StateAnxietyPreRepository stateAnxietyPre_Repository;
    private StateAnxietyPostRepository stateAnxietyPost_Repository;
    private static final Logger LOG = LoggerFactory.getLogger(QuestionController.class);

    /**
     * Spring automatically configures this object.
     * You can modify the location of this database by editing the application.properties file.
     */
    @Autowired
    public QuestionController(DASS21_ASRepository dass21_asRepository,
                              DASS21_DSRepository dass21_dsRepository,
                              QOLRepository qol_Repository,
                              ImpactAnxiousImagery_Repository impact_Repository,
                              MentalHealthHxTxRepository mh_Repository,
                              MultiUserExperienceRepository mue_Repository,
                              PilotUserExperienceRepository pue_Repository,
                              AUDIT_Repository audit_Repository,
                              CredibilityRepository credibilityRepository,
                              DemographicRepository demographicRepository,
                              ParticipantRepository participantRepository,
                              AnxiousImageryPrime_Repository anxiousImageryPrime_Repository,
                              NeutralImageryPrime_Repository neutralImageryPrime_Repository,
                              StateAnxietyRepository stateAnxiety_Repository,
//                              StateAnxietyPreRepository stateAnxietyPre_Repository,
                              FollowUp_ChangeInTreatment_Repository followup_Repository,
                              StateAnxietyPostRepository stateAnxietyPost_Repository) {
        this.dass21_asRepository = dass21_asRepository;
        this.credibilityRepository = credibilityRepository;
        this.demographicRepository = demographicRepository;
        this.participantRepository = participantRepository;
        this.audit_Repository = audit_Repository;
        this.dass21_dsRepository = dass21_dsRepository;
        this.qol_Repository = qol_Repository;
        this.impact_Repository = impact_Repository;
        this.mh_Repository = mh_Repository;
        this.mue_Repository = mue_Repository;
        this.pue_Repository = pue_Repository;
        this.anxiousImageryPrime_Repository = anxiousImageryPrime_Repository;
        this.neutralImageryPrime_Repository = neutralImageryPrime_Repository;
        this.stateAnxiety_Repository = stateAnxiety_Repository;
//        this.stateAnxietyPre_Repository = stateAnxietyPre_Repository;
        this.stateAnxietyPost_Repository = stateAnxietyPost_Repository;
        this.followup_Repository = followup_Repository;
    }

    /**
     * Does some tasks common to all forms:
     * - Adds the current session name to the data being recorded
     * - Marks this "task" as complete, and moves the participant on to the next session
     * - Connects the data to the participant who completed it.
     *
     * @param data
     */
    private void recordSessionProgress(QuestionnaireData data) {

        ParticipantDAO dao = (ParticipantDAO) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dao = participantRepository.findByEmail(dao.getEmail()); // Refresh session object from database.
        Participant participant = participantRepository.entityToDomain(dao);

        // Record the session for which this questionnaire was completed.
        data.setSession(participant.getCurrentSession().getName());

        // Update the participant's session status, and save back to the database.
        participant.completeCurrentTask();
        participantRepository.domainToEntity(participant, dao);
        participantRepository.save(dao);

        // Connect the participant to the data being recorded.
        data.setParticipantDAO(dao);
        data.setDate(new Date());
    }

    /**
     * DASS 21 AS
     * ---------*
     */
    @RequestMapping(value = "DASS21_AS", method = RequestMethod.GET)
    public ModelAndView showDASS21_AS() {
        return new ModelAndView("/questions/DASS21_AS", "DASS21_AS", new DASS21_AS());
    }

    @RequestMapping(value = "DASS21_AS", method = RequestMethod.POST)
    RedirectView handleDASS21_AS(@ModelAttribute("DASS21_AS") DASS21_AS dass21_as,
                                 BindingResult result) {

        recordSessionProgress(dass21_as);
        dass21_asRepository.save(dass21_as);
        return new RedirectView("/session");
    }

    @RequestMapping(value = "DASS21_AS/export", method = RequestMethod.GET, produces = "text/csv")
    @ResponseBody // Return the string directly, the return value is not a template name.
    String exportDASS21_AS() {
        return(objectListToCSV(dass21_asRepository.findAll()));
    }


    /**
     * DASS 21 DS
     * ---------*
     */
    @RequestMapping(value = "DASS21_DS", method = RequestMethod.GET)
    public ModelAndView showDASS21_DS() {
        return new ModelAndView("/questions/DASS21_DS", "DASS21_DS", new DASS21_DS());
    }

    @RequestMapping(value = "DASS21_DS", method = RequestMethod.POST)
    RedirectView handleDASS21_DS(@ModelAttribute("DASS21_DS") DASS21_DS dass21_ds,
                                 BindingResult result) {

        recordSessionProgress(dass21_ds);
        dass21_dsRepository.save(dass21_ds);
        return new RedirectView("/session");
    }

    @RequestMapping(value = "DASS21_DS/export", method = RequestMethod.GET, produces = "text/csv")
    @ResponseBody // Return the string directly, the return value is not a template name.
    String exportDASS21_DS() {
        return(objectListToCSV(dass21_dsRepository.findAll()));
    }



    /**
     * QOL
     * ---------*
     */
    @RequestMapping(value = "QOL", method = RequestMethod.GET)
    public ModelAndView showqol() {
        return new ModelAndView("questions/QOL", "QOL", new QOL());
    }

    @RequestMapping(value = "QOL", method = RequestMethod.POST)
    RedirectView handleqol(@ModelAttribute("qol") QOL qol,
                           BindingResult result) {

        recordSessionProgress(qol);
        qol_Repository.save(qol);
        return new RedirectView("/session");
    }

    @RequestMapping(value = "QOL/export", method = RequestMethod.GET, produces = "text/csv")
    @ResponseBody // Return the string directly, the return value is not a template name.
    String exportQOL() {
        return(objectListToCSV(qol_Repository.findAll()));
    }


    /**
     * AUDIT
     * ---------*
     */
    @RequestMapping(value = "audit", method = RequestMethod.GET)
    public ModelAndView showaudit() {
        return new ModelAndView("/questions/audit", "aduit", new AUDIT());
    }

    @RequestMapping(value = "aduit", method = RequestMethod.POST)
    RedirectView handleaudit(@ModelAttribute("audit") AUDIT audit,
                             BindingResult result) {

        recordSessionProgress(audit);
        audit_Repository.save(audit);
        return new RedirectView("/session");
    }

    @RequestMapping(value = "audit/export", method = RequestMethod.GET, produces = "text/csv")
    @ResponseBody // Return the string directly, the return value is not a template name.
    String exportAudit() {
        return(objectListToCSV(audit_Repository.findAll()));
    }


    /**
     * Credibility
     * ---------*
     */
    @RequestMapping(value = "credibility", method = RequestMethod.GET)
    public ModelAndView showCredibility() {
        return new ModelAndView("/questions/credibility", "credibility", new Credibility());
    }

    @RequestMapping(value = "credibility", method = RequestMethod.POST)
    RedirectView handleCredibility(@ModelAttribute("credibility") Credibility credibility,
                                   BindingResult result) {

        recordSessionProgress(credibility);
        credibilityRepository.save(credibility);
        return new RedirectView("/session");
    }

    @RequestMapping(value = "credibility/export", method = RequestMethod.GET, produces = "text/csv")
    @ResponseBody // Return the string directly, the return value is not a template name.
    String exportCredibility() {
        return(objectListToCSV(credibilityRepository.findAll()));
    }


    /**
     * FollowUp_ChangeInTreatment
     * ---------*
     */

    @RequestMapping(value = "FU", method = RequestMethod.GET)
    public ModelAndView showFollowUp() {
        return new ModelAndView("/questions/FU", "FU", new FollowUp_ChangeInTreatment());
    }

    @RequestMapping(value = "FU", method = RequestMethod.POST)
    RedirectView handleFollowUp(@ModelAttribute("FU") FollowUp_ChangeInTreatment followup,
                                BindingResult result) {

        recordSessionProgress(followup);
        followup_Repository.save(followup);
        return new RedirectView("/session");
    }

    @RequestMapping(value = "FU/export", method = RequestMethod.GET, produces = "text/csv")
    @ResponseBody // Return the string directly, the return value is not a template name.
    String exportFu() {
        return(objectListToCSV(followup_Repository.findAll()));
    }

    /**
     * MentalHealthHxTx
     * ---------*
     */

    @RequestMapping(value = "MH", method = RequestMethod.GET)
    public ModelAndView showMentalHealthHxTx() {
        return new ModelAndView("/questions/MH", "MH", new MentalHealthHxTx());
    }

    @RequestMapping(value = "MH", method = RequestMethod.POST)
    RedirectView handleMentalHealthHxTx(@ModelAttribute("MH") MentalHealthHxTx mh,
                                        BindingResult result) {

        recordSessionProgress(mh);
        mh_Repository.save(mh);
        return new RedirectView("/session");
    }

    @RequestMapping(value = "MH/export", method = RequestMethod.GET, produces = "text/csv")
    @ResponseBody // Return the string directly, the return value is not a template name.
    String exportHealthHxTx() {
        return(objectListToCSV(mh_Repository.findAll()));
    }


    /**
     * MultiUserExperience
     * ---------*
     */

    @RequestMapping(value = "MUE", method = RequestMethod.GET)
    public ModelAndView showMultiUserExperience() {
        return new ModelAndView("/questions/MUE", "MUE", new MultiUserExperience());
    }

    @RequestMapping(value = "MUE", method = RequestMethod.POST)
    RedirectView handleMultiUserExperience(@ModelAttribute("MUE") MultiUserExperience mue,
                                           BindingResult result) {

        recordSessionProgress(mue);
        mue_Repository.save(mue);
        return new RedirectView("/session");
    }

    @RequestMapping(value = "MUE/export", method = RequestMethod.GET, produces = "text/csv")
    @ResponseBody // Return the string directly, the return value is not a template name.
    String exportMUE() {
        return(objectListToCSV(mue_Repository.findAll()));
    }


    /**
     * PilotUserExperience
     * ---------*
     */

    @RequestMapping(value = "PUE", method = RequestMethod.GET)
    public ModelAndView showPilotUserExperience() {
        return new ModelAndView("questions/PUE", "PUE", new PilotUserExperience());
    }

    @RequestMapping(value = "PUE", method = RequestMethod.POST)
    RedirectView handlePilotUserExperience(@ModelAttribute("PUE") PilotUserExperience pue,
                                           BindingResult result) {

        recordSessionProgress(pue);
        pue_Repository.save(pue);
        return new RedirectView("/session");
    }

    @RequestMapping(value = "PUE/export", method = RequestMethod.GET, produces = "text/csv")
    @ResponseBody // Return the string directly, the return value is not a template name.
    String exportPUE() {
        return(objectListToCSV(pue_Repository.findAll()));
    }


    /**
     * ImpactAnxiousImagery
     * ---------*
     */

    @RequestMapping(value = "impact", method = RequestMethod.GET)
    public ModelAndView showImpact() {
        return new ModelAndView("/questions/impact", "impact", new ImpactAnxiousImagery());
    }

    @RequestMapping(value = "impact", method = RequestMethod.POST)
    RedirectView handleImpact(@ModelAttribute("impact") ImpactAnxiousImagery impact,
                              BindingResult result) {

        recordSessionProgress(impact);
        impact_Repository.save(impact);
        return new RedirectView("/session");
    }

    @RequestMapping(value = "impact/export", method = RequestMethod.GET, produces = "text/csv")
    @ResponseBody // Return the string directly, the return value is not a template name.
    String exportImplact() {
        return(objectListToCSV(impact_Repository.findAll()));
    }


    /**
     * AnxiousImageryPrime
     * ---------*
     */

    @RequestMapping(value = "AIP", method = RequestMethod.GET)
    public ModelAndView showAIP() {
        return new ModelAndView("/questions/AIP", "AIP", new AnxiousImageryPrime());
    }

    @RequestMapping(value = "AIP", method = RequestMethod.POST)
    RedirectView handleAIP(@ModelAttribute("AIP") AnxiousImageryPrime prime,
                              BindingResult result) {

        recordSessionProgress(prime);
        anxiousImageryPrime_Repository.save(prime);
        return new RedirectView("/session");
    }

    @RequestMapping(value = "AIP/export", method = RequestMethod.GET, produces = "text/csv")
    @ResponseBody // Return the string directly, the return value is not a template name.
    String exportAIP() {
        return(objectListToCSV(anxiousImageryPrime_Repository.findAll()));
    }


    /**
     * NeutralImageryPrime
     * ---------*
     */

    @RequestMapping(value = "NIP", method = RequestMethod.GET)
    public ModelAndView showNIP() {
        return new ModelAndView("/questions/NIP", "NIP", new NeutralImageryPrime());
    }

    @RequestMapping(value = "NIP", method = RequestMethod.POST)
    RedirectView handleNIP(@ModelAttribute("NIP") NeutralImageryPrime prime,
                              BindingResult result) {

        recordSessionProgress(prime);
        neutralImageryPrime_Repository.save(prime);
        return new RedirectView("/session");
    }

    @RequestMapping(value = "NIP/export", method = RequestMethod.GET, produces = "text/csv")
    @ResponseBody // Return the string directly, the return value is not a template name.
    String exportNIP() {
        return(objectListToCSV(neutralImageryPrime_Repository.findAll()));
    }


    /**
     * Demographics
     * ---------*
     */
    @RequestMapping(value = "demographics", method = RequestMethod.GET)
    public String showDemographics() {
        return "/questions/demographics";
    }

    @RequestMapping(value = "demographics", method = RequestMethod.POST)
    RedirectView handleDemographics(@ModelAttribute("demographics") Demographic demographic,
                                    BindingResult result) {

        recordSessionProgress(demographic);
        demographicRepository.save(demographic);
        return new RedirectView("/session");
    }

    @RequestMapping(value = "demographics/export", method = RequestMethod.GET, produces = "text/csv")
    @ResponseBody // Return the string directly, the return value is not a template name.
    String exportDemographics() {
        return(objectListToCSV(demographicRepository.findAll()));
    }


    /**
     * StateAnxietyRepository
     * ---------*
     */
    @RequestMapping(value = "SA", method = RequestMethod.GET)
    public ModelAndView showSAP() {
        return new ModelAndView("/questions/SA", "SA", new StateAnxiety());
    }

    @RequestMapping(value = "SA", method = RequestMethod.POST)
    RedirectView handleSAP(@ModelAttribute("SA") StateAnxiety sa,
                              BindingResult result) {

        recordSessionProgress(sa);
        stateAnxiety_Repository.save(sa);
        return new RedirectView("/session");

    }

    @RequestMapping(value = "SA/export", method = RequestMethod.GET, produces = "text/csv")
    @ResponseBody // Return the string directly, the return value is not a template name.
    String exportSAP() {
        return(objectListToCSV(stateAnxiety_Repository.findAll()));
    }


//    /**
//     * AnxietyPreRepository
//     * ---------*
//     */
//    @RequestMapping(value = "SAPr", method = RequestMethod.GET)
//    public ModelAndView showSAPr() {
//        return new ModelAndView("questions/SAPr", "SAPr", new StateAnxiety());
//    }
//
//    @RequestMapping(value = "SAPr", method = RequestMethod.POST)
//    RedirectView handleSAPr(@ModelAttribute("SAPr") StateAnxietyPre SAPr,
//                              BindingResult result) {
//
//        recordSessionProgress(SAPr);
//        stateAnxietyPre_Repository.save(SAPr);
//        return new RedirectView("/session");
//
//    }

    /**
     * StateAnxietypostRepository
     * ---------*
     */
    @RequestMapping(value = "SAPo", method = RequestMethod.GET)
    public ModelAndView showSAPo() {
        return new ModelAndView("/questions/SAPo", "SAPo", new StateAnxiety());
    }

    @RequestMapping(value = "SAPo", method = RequestMethod.POST)
    RedirectView handleSAPo(@ModelAttribute("SAPo") StateAnxietyPost SAPost,
                              BindingResult result) {

        recordSessionProgress(SAPost);
        stateAnxietyPost_Repository.save(SAPost);
        return new RedirectView("/session");

    }

    @RequestMapping(value = "SAPo/export", method = RequestMethod.GET, produces = "text/csv")
    @ResponseBody // Return the string directly, the return value is not a template name.
    String exportSAPo() {
        return(objectListToCSV(stateAnxietyPost_Repository.findAll()));
    }



    /** ==============================================================
     *       Some utilility methods for exporting csv data from the forms
     *  ==============================================================
     */

    /**
     * Converts a list of objects into a string suitable for returning
     * as a csv.
     * @param objects
     * @return
     */
    public static String objectListToCSV(List objects) {
        StringBuffer csv = new StringBuffer();

        if(objects.size() < 1) return "";

        Method[] methods = objects.get(0).getClass().getMethods();

        // Add in headers.
        for(Method method : methods){
            if(isGetter(method)) {
                csv.append("\"");
                csv.append(method.getName().substring(3));
                csv.append("\"");
                csv.append(",");
            }
        }
        csv.append("\n");
        for(Object o : objects) {
            appendObjectToCSV(o, csv);
            csv.append("\n");
        }
        return csv.toString();
    }

    private static void appendObjectToCSV(Object o, StringBuffer csv) {
        Method[] methods = o.getClass().getMethods();
        ParticipantDAO participantDAO;
        Session.NAME session;
        List list;
        String data;

        for(Method method : methods){
            if(isGetter(method)) {
                try {
                    if(null == method.invoke(o)) {
                        data = "";
                    } else if(method.getReturnType().isPrimitive()) {
                        data = method.invoke(o).toString();
                    } else if (String.class.equals(method.getReturnType())) {
                        data = method.invoke(o).toString();
                    } else if (List.class.equals(method.getReturnType())) {
                        StringBuffer values = new StringBuffer();
                        list = (List)method.invoke(o);
                        for(int i = 0; i < list.size(); i++) {
                            values.append(list.get(i).toString());
                            if (i < list.size() -1) values.append("; ");
                        }
                        data = values.toString();
                    } else if (Date.class.equals(method.getReturnType())) {
                        data = method.invoke(o).toString();
                    } else if (ParticipantDAO.class.equals(method.getReturnType())) {
                        participantDAO = (ParticipantDAO)method.invoke(o);
                        data = "" + participantDAO.getId();
                    } else if (Session.NAME.class.equals(method.getReturnType())) {
                        session  = (Session.NAME)method.invoke(o);
                        data = session.toString();
                    } else if (Class.class.equals(method.getReturnType())) {
                        data = ((Class)method.invoke(o)).getSimpleName();
                    } else {
                        data = method.getReturnType().getName();
                    }
                    csv.append("\"");
                    csv.append(data.replaceAll("\"", "\\\""));
                    csv.append("\"");
                    csv.append(",");
                } catch (InvocationTargetException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (IllegalAccessException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }

    }

    public static boolean isGetter(Method method){
        if(!method.getName().startsWith("get"))       return false;
        if(method.getParameterTypes().length != 0)    return false;
        if(void.class.equals(method.getReturnType())) return false;
        return true;
    }




}

