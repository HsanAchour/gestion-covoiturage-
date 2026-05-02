/* ============================================================
   IHEC Ride — Mock offline (vide par défaut)
   Les pages se rempliront à partir de vos propres données
   dès que vous utiliserez l'application.
   ============================================================ */

const MOCK = {
    trajets:      [],
    reservations: [],
    messages:     [],
    notifications:[],
    utilisateurs: [],
    evaluations:  [],
    reclamations: [],
    groupes:      [],
    stats: {
        usersTotal: 0, passagers: 0, conducteurs: 0, admins: 0,
        trajetsTotal: 0, trajetsTermines: 0,
        reservationsTotal: 0, reclamationsOuvertes: 0,
        co2: 0, trajetsMois: [0,0,0,0,0,0,0]
    }
};
