using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Data;
using System.Data.SqlClient;
using System.Configuration;
using System.Linq.Expressions;

namespace ApiGardilcic.Models
{
    public class Conectar : IDisposable
    {


        public SqlConnection Conexion { get; set; }
        public SqlTransaction Transaccion { get; set; }

        private bool EscribirLogEventVwr = false;

        public Conectar(string db)
        {
            var cadena = ConfigurationManager.ConnectionStrings[db].ConnectionString;


            Conexion = new SqlConnection(cadena);

        }

        public Conectar()
        {
            var cadena = ConfigurationManager.ConnectionStrings["GardiSoftConnection"].ConnectionString;
            Conexion = new SqlConnection(cadena);

        }



        public void Abrir()
        {
            if (Conexion.State != ConnectionState.Open)
            {
                Conexion.Open();
            }
        }



        public void Cerrar()
        {
            if (Transaccion == null)
            {
                //cmd.Transaction = Transaccion;
                Conexion.Close();
            }

        }


        public void Dispose()
        {
            Conexion.Close();
        }


        public DataTable EjecutarConsultaSelect(string sql, CommandType tipo, params SqlParameter[] parametros)
        {
            var cmd = Conexion.CreateCommand();

            cmd.CommandTimeout = int.MaxValue;
            if (Transaccion != null)
            {
                cmd.Transaction = Transaccion;
            }
            cmd.CommandText = sql;
            cmd.CommandType = tipo;


            if (parametros != null)
            {
                cmd.Parameters.AddRange(parametros);
            }

            DataTable tabla = new DataTable("resultado");

            try
            {
                SqlDataAdapter da = new SqlDataAdapter(cmd);
                da.Fill(tabla);
                this.Cerrar();
            }
            catch (Exception ex)
            {


            }

            return tabla;
        }


        public DataTable EjecutarConsultaNoSelectString(string sql, CommandType tipo, params SqlParameter[] parametros)
        {

            //int resultado = 0;
            var cmd = Conexion.CreateCommand();
            cmd.CommandTimeout = int.MaxValue;
            if (Transaccion != null)
            {
                cmd.Transaction = Transaccion;
            }
            cmd.CommandText = sql;
            cmd.CommandType = tipo;

            if (parametros != null)
            {
                cmd.Parameters.AddRange(parametros);
            }

            DataTable tabla = new DataTable("subproyectos");

            try
            {
                SqlDataAdapter da = new SqlDataAdapter(cmd);
                da.Fill(tabla);
                this.Cerrar();
            }
            catch (Exception ex)
            {

            }

            return tabla;

        }

        public DataTable EjecutaFuncionSQL(string sql, CommandType tipo, params SqlParameter[] parametros)
        {
            var cmd = Conexion.CreateCommand();
            if (Transaccion != null)
            {
                cmd.Transaction = Transaccion;
            }
            cmd.CommandText = sql;
            DataTable tabla = new DataTable("subproyectos");
            try
            {
                Abrir();
                cmd.ExecuteReader();
                SqlDataAdapter da = new SqlDataAdapter(cmd);
                da.Fill(tabla);
                this.Cerrar();
            }
            catch (Exception ex)
            {
                Cerrar();

                throw ex;

            }
            Cerrar();
            return tabla;
        }

        public int EjecutarConsultaNoSelect(string sql, CommandType tipo, params SqlParameter[] parametros)
        {
            int resultado = 0;
            var cmd = Conexion.CreateCommand();
            cmd.CommandTimeout = int.MaxValue;

            if (Transaccion != null)
            {
                cmd.Transaction = Transaccion;
            }

            cmd.CommandText = sql;
            cmd.CommandType = tipo;

            if (parametros != null)
            {
                cmd.Parameters.AddRange(parametros);
            }

            try
            {
                Abrir();
                resultado = cmd.ExecuteNonQuery(); // Ejecuta la consulta que no devuelve resultados
            }
            catch (Exception ex)
            {
                Cerrar();
                throw ex; // Lanza la excepción en caso de error
            }

            Cerrar();
            return resultado;
        }


    }
}