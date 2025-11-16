import tkinter as tk
from tkinter import ttk, messagebox
import threading
import requests
from ttkbootstrap import Style
from tkintermapview import TkinterMapView
import math

##################  CONFIG 
API_URL = "http://127.0.0.1:8000"
TIMEOUT = 8
MADRID_CENTER = (40.4168, -3.7038)
NEARBY_LIMIT = 10

FAVORITOS_ENDPOINT = f"{API_URL}/favoritos"  # GET lista, POST parking, DELETE/{id}

################### HELPERS 
def fetch_parkings(q=None, emt=None):
    params = {}
    if q:
        params["q"] = q
    if emt is not None:
        params["emt"] = "true" if emt else "false"
    r = requests.get(f"{API_URL}/parkings", params=params, timeout=TIMEOUT)
    r.raise_for_status()
    return r.json()

def fetch_nearby(lat, lon, limit=NEARBY_LIMIT):
    params = {"lat": lat, "lon": lon, "limit": limit}
    r = requests.get(f"{API_URL}/parkings/nearby", params=params, timeout=TIMEOUT)
    r.raise_for_status()
    return r.json()

def fetch_favorites():
    r = requests.get(FAVORITOS_ENDPOINT, timeout=TIMEOUT)
    r.raise_for_status()
    return r.json()

def add_favorite(parking_data: dict):
    # eliminar cualquier campo de distancia antes de mandar al backend
    clean = {
        k: v
        for k, v in parking_data.items()
        if k.lower() not in ("distance", "distancia")
    }
    r = requests.post(FAVORITOS_ENDPOINT, json=clean, timeout=TIMEOUT)
    r.raise_for_status()
    return r.json()

def delete_favorite(identifier):
    url = f"{FAVORITOS_ENDPOINT}/{identifier}"
    r = requests.delete(url, timeout=TIMEOUT)
    r.raise_for_status()
    return r.status_code in (200, 204)

def haversine(lat1, lon1, lat2, lon2):
    """Distancia entre dos coordenadas (en metros)."""
    R = 6371000
    phi1, phi2 = math.radians(lat1), math.radians(lat2)
    dphi = math.radians(lat2 - lat1)
    dlambda = math.radians(lon2 - lon1)
    a = math.sin(dphi / 2) ** 2 + math.cos(phi1) * math.cos(phi2) * math.sin(dlambda / 2) ** 2
    return R * 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))

################### APP 
class ParkingFinderApp(tk.Tk):
    def __init__(self):
        super().__init__()
        Style("flatly")
        self.title("🚗 Parking Finder — Grupo 20")
        self.geometry("1300x800")
        self.minsize(1000, 650)
        self.configure(bg="#f9fafb")

        # Estado
        self.q_var = tk.StringVar()
        self.emt_state = tk.StringVar(value="— cualquier —")
        self.auto_click = tk.BooleanVar(value=True)
        self.nearby_limit = tk.IntVar(value=NEARBY_LIMIT)  # nº parkings cercanos
        self.markers = []
        self.user_marker = None
        self.user_coords = None
        self.current_results = []   # lista de parkings actualmente mostrados

        # Ventana de favoritos
        self.fav_win = None
        self.fav_tree = None
        self.fav_data = []

        # Construir interfaz
        self._build_ui()
        self.map.set_position(*MADRID_CENTER)
        self.map.set_zoom(12)

    ################### UI 
    def _build_ui(self):
        # --- Top bar ---
        top = ttk.Frame(self, padding=10)
        top.pack(side="top", fill="x")

        ttk.Label(top, text="🚘 Parking Finder — Grupo 20", font=("SF Pro Display", 18, "bold")).pack(side="left")
        ttk.Checkbutton(top, text="Auto-buscar al clicar", variable=self.auto_click).pack(side="right", padx=6)
        
        ttk.Label(
        top,
        text=" Data sourced from EMT Madrid Open Data (Aparcamientos EMT dataset), licensed under CC-BY 4.0. \n https://datos.emtmadrid.es/dataset/aparcamientos-emt",
        font=("SF Pro Display", 8, "italic")
        # si quieres aún más discreto:
        # foreground="#6b7280"
        ).pack(side="top", anchor="w")
        # --- Main layout ---
        body = ttk.Frame(self)
        body.pack(side="top", expand=True, fill="both")
        body.columnconfigure(1, weight=1)
        body.rowconfigure(0, weight=1)

        # --- Sidebar ---
        self.sidebar = ttk.Frame(body, padding=16)
        self.sidebar.grid(row=0, column=0, sticky="ns")
        self.sidebar.columnconfigure(0, weight=1)

        ttk.Label(self.sidebar, text="🔍 Búsqueda de Parkings", font=("SF Pro Display", 14, "bold")).grid(row=0, column=0, sticky="w", pady=(0, 8))
        ttk.Label(self.sidebar, text="Nombre o calle:", font=("SF Pro Display", 11)).grid(row=1, column=0, sticky="w")
        ttk.Entry(self.sidebar, textvariable=self.q_var, font=("SF Pro Display", 11)).grid(row=2, column=0, sticky="ew", pady=(0, 8))

        ttk.Label(self.sidebar, text="Filtro EMT:", font=("SF Pro Display", 11)).grid(row=3, column=0, sticky="w")
        ttk.Combobox(
            self.sidebar,
            textvariable=self.emt_state,
            state="readonly",
            values=["— cualquier —", "Solo EMT", "No EMT"]
        ).grid(row=4, column=0, sticky="ew", pady=(0, 8))

        btns = ttk.Frame(self.sidebar)
        btns.grid(row=5, column=0, sticky="ew", pady=(2, 10))
        ttk.Button(btns, text="Buscar", command=self._on_search).pack(side="left", expand=True, fill="x", padx=(0, 4))
        ttk.Button(btns, text="Reiniciar", command=self._on_reset).pack(side="left", expand=True, fill="x")

        ttk.Separator(self.sidebar).grid(row=6, column=0, sticky="ew", pady=12)
        ttk.Label(
            self.sidebar,
            text="🗺️ Clic en el mapa para buscar cercanos",
            wraplength=210,
            justify="left",
            font=("SF Pro Display", 10, "italic")
        ).grid(row=7, column=0, sticky="w")

        # Selector nº de parkings cercanos
        ttk.Label(
            self.sidebar,
            text="Nº parkings cercanos:",
            font=("SF Pro Display", 10)
        ).grid(row=8, column=0, sticky="w", pady=(6, 0))

        ttk.Spinbox(
            self.sidebar,
            from_=1,
            to=50,
            textvariable=self.nearby_limit,
            width=6
        ).grid(row=9, column=0, sticky="w", pady=(0, 8))

        ttk.Separator(self.sidebar).grid(row=10, column=0, sticky="ew", pady=10)

        # Título + botones de favoritos
        title_frame = ttk.Frame(self.sidebar)
        title_frame.grid(row=11, column=0, sticky="ew", pady=(0, 4))
        ttk.Label(title_frame, text="📍 Parkings cercanos", font=("SF Pro Display", 13, "bold")).pack(side="left")

        fav_btns = ttk.Frame(self.sidebar)
        fav_btns.grid(row=12, column=0, sticky="ew", pady=(0, 4))
        ttk.Button(
            fav_btns,
            text="⭐ Añadir a favoritos",
            command=self._on_add_favorite
        ).pack(side="left", expand=True, fill="x", padx=(0, 4))
        ttk.Button(
            fav_btns,
            text="Ver favoritos",
            command=self._open_favorites_window
        ).pack(side="left", expand=True, fill="x")

        # Tabla de distancias
        columns = ("name", "distance")
        self.tree = ttk.Treeview(self.sidebar, columns=columns, show="headings", height=16)
        self.tree.heading("name", text="Nombre")
        self.tree.heading("distance", text="Distancia")
        self.tree.column("name", width=180)
        self.tree.column("distance", width=80, anchor="e")
        self.tree.grid(row=13, column=0, sticky="nsew", pady=(6, 0))

        # Eventos: seleccionar y doble clic
        self.tree.bind("<<TreeviewSelect>>", self._on_tree_select)
        self.tree.bind("<Double-1>", self._on_tree_double_click)

        # --- Mapa ---
        map_frame = ttk.Frame(body)
        map_frame.grid(row=0, column=1, sticky="nsew")
        map_frame.columnconfigure(0, weight=1)
        map_frame.rowconfigure(0, weight=1)

        self.map = TkinterMapView(map_frame, corner_radius=0)
        self.map.grid(row=0, column=0, sticky="nsew")
        self.map.add_left_click_map_command(self._on_map_click)

    ################### EVENTOS 
    def _on_search(self):
        q = self.q_var.get().strip() or None
        emt = None
        if self.emt_state.get() == "Solo EMT":
            emt = True
        elif self.emt_state.get() == "No EMT":
            emt = False
        threading.Thread(target=self._search_thread, args=(q, emt), daemon=True).start()

    def _on_reset(self):
        self.q_var.set("")
        self.emt_state.set("— cualquier —")
        self._clear_markers()
        if self.user_marker:
            try:
                self.user_marker.delete()
            except Exception:
                pass
            self.user_marker = None
        self.user_coords = None
        self.current_results = []
        self.tree.delete(*self.tree.get_children())
        self.map.set_position(*MADRID_CENTER)
        self.map.set_zoom(12)

    def _on_map_click(self, coordinate):
        # Siempre hay SOLO un marcador de usuario
        if isinstance(coordinate, tuple):
            lat, lon = coordinate
        else:
            lat, lon = coordinate.lat, coordinate.lng

        self.user_coords = (lat, lon)

        if self.user_marker:
            try:
                self.user_marker.delete()
            except Exception:
                pass
            self.user_marker = None

        # marcador nativo verde
        self.user_marker = self.map.set_marker(
            lat, lon,
            text="Tú",
            marker_color_circle="#10B981",
            marker_color_outside="#064E3B",
        )

        if self.auto_click.get():
            try:
                limit = int(self.nearby_limit.get())
                if limit < 1:
                    limit = NEARBY_LIMIT
            except Exception:
                limit = NEARBY_LIMIT

            threading.Thread(
                target=self._nearby_thread,
                args=(lat, lon, limit),
                daemon=True
            ).start()

    ################### THREADS 
    def _search_thread(self, q, emt):
        try:
            data = fetch_parkings(q, emt)
        except Exception as e:
            self.after(0, lambda: messagebox.showerror("Error", str(e)))
            return
        self.after(0, lambda: self._draw_parking_markers(data, center_on_first=True))

    def _nearby_thread(self, lat, lon, limit):
        try:
            data = fetch_nearby(lat, lon, limit=limit)
        except Exception as e:
            self.after(0, lambda: messagebox.showerror("Error", str(e)))
            return
        self.after(0, lambda: self._draw_parking_markers(data, center_on_first=True))

    def _favorites_thread(self):
        try:
            data = fetch_favorites()
        except Exception as e:
            self.after(0, lambda: messagebox.showerror("Error", f"No se pudieron cargar los favoritos:\n{e}"))
            return
        self.after(0, lambda: self._update_favorites_view(data))

    def _add_favorite_thread(self, parking_payload):
        try:
            add_favorite(parking_payload)
        except Exception as e:
            self.after(0, lambda: messagebox.showerror("Error", f"No se pudo añadir a favoritos:\n{e}"))
            return
        self.after(0, lambda: messagebox.showinfo("Favoritos", "Parking añadido a favoritos."))

    def _delete_favorite_thread(self, identifier):
        try:
            delete_favorite(identifier)
        except Exception as e:
            self.after(0, lambda: messagebox.showerror("Error", f"No se pudo eliminar el favorito:\n{e}"))
            return
        self.after(0, self._refresh_favorites)

    ################### MAP & LIST 
    def _clear_markers(self):
        for m in self.markers:
            try:
                m.delete()
            except Exception:
                pass
        self.markers.clear()

    def _highlight_parking(self, lat, lon, name, dist=None):
        """Elimina todos los marcadores (menos el usuario) y deja solo este resaltado."""
        self._clear_markers()
        m = self.map.set_marker(
            lat, lon,
            text=name[:25],
            marker_color_circle="#2563EB",   # azul
            marker_color_outside="#1E3A8A",
        )
        if dist is not None:
            m.tooltip_text = f"{name}\n{int(dist)} m"
        else:
            m.tooltip_text = name
        self.markers.append(m)
        self.map.set_position(lat, lon)
        self.map.set_zoom(16)

    def _draw_parking_markers(self, parkings, center_on_first=False):
        self._clear_markers()
        self.tree.delete(*self.tree.get_children())
        self.current_results = []

        if not parkings:
            messagebox.showinfo("Sin resultados", "No se encontraron parkings.")
            return

        user_lat, user_lon = self.user_coords if self.user_coords else MADRID_CENTER

        results = []
        for p in parkings:
            lat = p.get("lat")
            lon = p.get("long") or p.get("lon")
            if lat is None or lon is None:
                continue

            name = p.get("name") or "Parking"
            dist = p.get("distance") or p.get("distancia")
            if dist is None:
                dist = haversine(user_lat, user_lon, lat, lon)
            try:
                dist_val = float(dist)
            except Exception:
                dist_val = haversine(user_lat, user_lon, lat, lon)

            results.append(
                {
                    "parking": p,
                    "distance": dist_val,
                    "name": name,
                    "lat": lat,
                    "lon": lon,
                }
            )

        results.sort(key=lambda x: x["distance"])
        self.current_results = results

        for item in self.current_results:
            lat = item["lat"]
            lon = item["lon"]
            name = item["name"]
            dist = item["distance"]

            m = self.map.set_marker(
                lat, lon,
                text="",
                marker_color_circle="#F97316",      # naranja/rojo
                marker_color_outside="#7C2D12",
            )
            m.tooltip_text = f"{name}\n{int(dist)} m"
            self.markers.append(m)
            self.tree.insert("", "end", values=(name[:28], f"{int(dist)} m"))

        if center_on_first and self.current_results:
            first = self.current_results[0]
            self.map.set_position(first["lat"], first["lon"])
            self.map.set_zoom(14)

    ################### DETALLES PARKING 
    def _show_parking_details(self, parking: dict, title: str):
        lines = []
        for k in sorted(parking.keys()):
            lines.append(f"{k}: {parking[k]}")
        text = "\n".join(lines) if lines else "Sin datos."

        win = tk.Toplevel(self)
        win.title(title)
        win.geometry("500x400")

        frame = ttk.Frame(win, padding=10)
        frame.pack(fill="both", expand=True)

        txt = tk.Text(frame, wrap="word")
        txt.pack(fill="both", expand=True)
        txt.insert("1.0", text)
        txt.config(state="disabled")

    ################### EVENTOS LISTA RESULTADOS 
    def _on_tree_select(self, event):
        sel = self.tree.selection()
        if not sel:
            return
        item_id = sel[0]
        children = list(self.tree.get_children())
        try:
            idx = children.index(item_id)
        except ValueError:
            return

        try:
            selected = self.current_results[idx]
        except IndexError:
            return

        lat = selected["lat"]
        lon = selected["lon"]
        name = selected["name"]
        dist = selected["distance"]
        self._highlight_parking(lat, lon, name, dist)

    def _on_tree_double_click(self, event):
        item_id = self.tree.identify_row(event.y)
        if not item_id:
            return

        children = list(self.tree.get_children())
        try:
            idx = children.index(item_id)
        except ValueError:
            return

        try:
            selected = self.current_results[idx]
        except IndexError:
            return

        # resaltar en mapa
        lat = selected["lat"]
        lon = selected["lon"]
        name = selected["name"]
        dist = selected["distance"]
        self._highlight_parking(lat, lon, name, dist)

        parking = selected["parking"]
        self._show_parking_details(parking, parking.get("name", "Detalles del parking"))

    ################### FAVORITOS 
    def _on_add_favorite(self):
        sel = self.tree.selection()
        if not sel:
            messagebox.showwarning("Favoritos", "Selecciona primero un parking de la lista.")
            return

        item_id = sel[0]
        children = list(self.tree.get_children())
        try:
            idx = children.index(item_id)
        except ValueError:
            messagebox.showerror("Error", "No se pudo determinar el elemento seleccionado.")
            return

        try:
            selected = self.current_results[idx]
        except IndexError:
            messagebox.showerror("Error", "El parking seleccionado ya no está en la lista.")
            return

        parking_payload = dict(selected["parking"])
        parking_payload.pop("distance", None)
        parking_payload.pop("distancia", None)

        threading.Thread(target=self._add_favorite_thread, args=(parking_payload,), daemon=True).start()

    def _open_favorites_window(self):
        if self.fav_win is not None and self.fav_win.winfo_exists():
            self.fav_win.lift()
            return

        self.fav_win = tk.Toplevel(self)
        self.fav_win.title("⭐ Parkings favoritos")
        self.fav_win.geometry("520x420")

        frame = ttk.Frame(self.fav_win, padding=10)
        frame.pack(fill="both", expand=True)

        ttk.Label(frame, text="⭐ Parkings favoritos", font=("SF Pro Display", 14, "bold")).pack(anchor="w")

        columns = ("name", "city")
        self.fav_tree = ttk.Treeview(frame, columns=columns, show="headings", height=15)
        self.fav_tree.heading("name", text="Nombre")
        self.fav_tree.heading("city", text="Ciudad")
        self.fav_tree.column("name", width=260)
        self.fav_tree.column("city", width=180)
        self.fav_tree.pack(fill="both", expand=True, pady=(8, 4))

        btn_frame = ttk.Frame(frame)
        btn_frame.pack(fill="x", pady=(4, 0))

        ttk.Button(btn_frame, text="🔄 Refrescar", command=self._refresh_favorites).pack(side="left", padx=(0, 4))
        ttk.Button(btn_frame, text="🗑 Eliminar seleccionado", command=self._on_delete_favorite).pack(side="left")

        # eventos: selección y doble clic en favoritos
        self.fav_tree.bind("<<TreeviewSelect>>", self._on_fav_select)
        self.fav_tree.bind("<Double-1>", self._on_fav_double_click)

        self._refresh_favorites()

    def _refresh_favorites(self):
        threading.Thread(target=self._favorites_thread, daemon=True).start()

    def _update_favorites_view(self, data):
        if not (self.fav_win and self.fav_win.winfo_exists()):
            return

        self.fav_data = list(data) if isinstance(data, list) else []
        self.fav_tree.delete(*self.fav_tree.get_children())

        for fav in self.fav_data:
            name = fav.get("name") or "Parking"
            city = fav.get("city") or fav.get("ncity") or ""
            self.fav_tree.insert("", "end", values=(str(name)[:40], str(city)[:30]))

    def _on_fav_select(self, event):
        sel = self.fav_tree.selection()
        if not sel:
            return
        item_id = sel[0]
        children = list(self.fav_tree.get_children())
        try:
            idx = children.index(item_id)
        except ValueError:
            return

        if idx < 0 or idx >= len(self.fav_data):
            return

        fav = self.fav_data[idx]
        lat = fav.get("lat")
        lon = fav.get("long") or fav.get("lon")
        if lat is None or lon is None:
            messagebox.showwarning("Favoritos", "Este favorito no tiene coordenadas para mostrar en el mapa.")
            return

        name = fav.get("name") or "Parking"
        self._highlight_parking(lat, lon, name)

    def _on_fav_double_click(self, event):
        item_id = self.fav_tree.identify_row(event.y)
        if not item_id:
            return

        children = list(self.fav_tree.get_children())
        try:
            idx = children.index(item_id)
        except ValueError:
            return

        if idx < 0 or idx >= len(self.fav_data):
            return

        fav = self.fav_data[idx]

        # resaltar en mapa
        lat = fav.get("lat")
        lon = fav.get("long") or fav.get("lon")
        if lat is not None and lon is not None:
            name = fav.get("name") or "Parking"
            self._highlight_parking(lat, lon, name)

        # mostrar detalles
        self._show_parking_details(fav, fav.get("name", "Detalles del parking favorito"))

    def _on_delete_favorite(self):
        if not (self.fav_tree and self.fav_win and self.fav_win.winfo_exists()):
            return

        sel = self.fav_tree.selection()
        if not sel:
            messagebox.showwarning("Favoritos", "Selecciona un favorito para eliminar.")
            return

        item_id = sel[0]
        children = list(self.fav_tree.get_children())
        try:
            idx = children.index(item_id)
        except ValueError:
            messagebox.showerror("Error", "No se pudo determinar el favorito seleccionado.")
            return

        try:
            fav = self.fav_data[idx]
        except IndexError:
            messagebox.showerror("Error", "El favorito seleccionado ya no existe en la lista.")
            return

        identifier = fav.get("id") or fav.get("identifier")
        if identifier is None:
            messagebox.showerror("Error", "El favorito no tiene un identificador válido para poder eliminarlo.")
            return

        if not messagebox.askyesno("Confirmar", "¿Seguro que quieres eliminar este parking de favoritos?"):
            return

        threading.Thread(target=self._delete_favorite_thread, args=(identifier,), daemon=True).start()

################### RUN 
if __name__ == "__main__":
    app = ParkingFinderApp()
    app.mainloop()
